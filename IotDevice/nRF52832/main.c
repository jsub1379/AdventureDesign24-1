#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include "nrf_sdh.h"
#include "nrf_sdh_ble.h"
#include "nrf_sdh_soc.h"
#include "nrf_pwr_mgmt.h"
#include "app_timer.h"
#include "boards.h"
#include "bsp.h"
#include "bsp_btn_ble.h"
#include "ble.h"
#include "ble_hci.h"
#include "ble_advertising.h"
#include "ble_conn_params.h"
#include "ble_db_discovery.h"
#include "ble_lbs_c.h"
#include "ble_gap.h"
#include "nrf_ble_gatt.h"
#include "nrf_ble_scan.h"
#include "ble_lbs_c.h"
#include "nrfx_twis.h"
#include "nrf_atomic.h"


#define CENTRAL_SCANNING_LED            BSP_BOARD_LED_0
#define CENTRAL_CONNECTED_LED           BSP_BOARD_LED_1
#define LEDBUTTON_LED                   BSP_BOARD_LED_2

#define SCAN_INTERVAL                   0x00A0  // 100 ms
#define SCAN_WINDOW                     0x0050  // 50 ms
#define SCAN_DURATION                   1000  // Scanning indefinitely

#define MIN_CONNECTION_INTERVAL         MSEC_TO_UNITS(7.5, UNIT_1_25_MS)
#define MAX_CONNECTION_INTERVAL         MSEC_TO_UNITS(30, UNIT_1_25_MS)
#define SLAVE_LATENCY                   0
#define SUPERVISION_TIMEOUT             MSEC_TO_UNITS(4000, UNIT_10_MS)

#define LEDBUTTON_BUTTON_PIN            BSP_BUTTON_0
#define BUTTON_DETECTION_DELAY          APP_TIMER_TICKS(50)

#define APP_BLE_CONN_CFG_TAG            1
#define APP_BLE_OBSERVER_PRIO           3

#define DEVICE_NAME_MAX_SIZE 20
#define DEVICE_TO_FIND_MAX 80


#define SDA_PIN             25  // SDA pin number
#define SCL_PIN             26  // SCL pin number


NRF_BLE_SCAN_DEF(m_scan);
BLE_LBS_C_DEF(m_ble_lbs_c);
NRF_BLE_GATT_DEF(m_gatt);
BLE_DB_DISCOVERY_DEF(m_db_disc);


#define TWIS_INSTANCE_ID 0
#define SLAVE_ADDR 0x04  // Set the TWI (I2C) slave address

typedef struct
{
    bool      is_not_empty;
    uint16_t  size;
    uint8_t   addr[BLE_GAP_ADDR_LEN];
    char      dev_name[DEVICE_NAME_MAX_SIZE];
    uint8_t   manuf_buffer[BLE_GAP_ADV_SET_DATA_SIZE_MAX];
    int8_t    rssi;
} scanned_device_t;
//i dont need this
typedef struct
{
    uint8_t* p_data;
    uint16_t  data_len;
} data_t;
static uint32_t device_number = 0;
static scanned_device_t m_device[DEVICE_TO_FIND_MAX];
scanned_device_t* scan_device_info_get(void)
{
    return m_device;
}



volatile int16_t threshold = -90;

static uint8_t RDBUF[4];
static uint8_t RDBUFIDX = 0;
static uint8_t RDCHAR[1];
static const nrfx_twis_t twis = NRFX_TWIS_INSTANCE(TWIS_INSTANCE_ID);
volatile nrf_atomic_u32_t response_mutex_flag = 0;
bool gda = false;
static uint32_t WRIDX = 0;
static uint32_t WRLEN = 0;
static char response[DEVICE_TO_FIND_MAX][13];


void set_flag(void) {
    nrf_atomic_u32_store(&response_mutex_flag, 1);
}

void clear_flag(void) {
    nrf_atomic_u32_store(&response_mutex_flag, 0);
}

bool check_flag(void) {
    return (nrf_atomic_u32_fetch_store(&response_mutex_flag, response_mutex_flag) == 1);
}

char rsp[] = "123456789ABCD";
char empty[] = "\0\0\0\0\0\0\0\0\0\0\0\0\0";
void twis_event_handler(nrfx_twis_evt_t const* p_event)
{
    switch (p_event->type)
    {
    case NRFX_TWIS_EVT_WRITE_DONE:
#ifdef DEBUG
        printf("WRITE DONE EVT\n");
#endif
        if (p_event->data.rx_amount > 0) {
#ifdef DEBUG
            printf("Received char: %c\n", RDCHAR[0]);
#endif
            RDBUF[RDBUFIDX] = RDCHAR[0];
            if (RDBUFIDX == 3 || RDBUF[RDBUFIDX] == 255) {
                if (RDBUF[0] == 'G' &&
                    RDBUF[1] == 'D' &&
                    RDBUF[2] == 'A' &&
                    RDBUF[3] == 255) {
#ifdef DEBUG
                    printf("GET DATA command received\n");
#endif
                    gda = true;
                    WRIDX = 0;
                    uint32_t expected = 0;
                    while (!nrf_atomic_u32_cmp_exch(&response_mutex_flag, &expected, 1)) {
                        expected = 0;
                    }
#ifdef DEBUG
                    printf("Preparing response, set mutex lock!\n");
#endif
                }
                else if (RDBUF[0] == 'S' &&
                    RDBUF[1] == 'T' &&
                    RDBUF[3] == 255) {
#ifdef DEBUG
                    printf("SET THRESHOLD command, Threshold set to %d\n", RDBUF[2]);
#endif
                    threshold = -RDBUF[2];
                }

                // Reset all buffer variables
                RDBUFIDX = 0;
                RDBUF[0] = 0;
                RDBUF[1] = 0;
                RDBUF[2] = 0;
                RDBUF[3] = 0;
            }
            else {
                RDBUFIDX++;
            }
        }
        break;

    case NRFX_TWIS_EVT_READ_REQ:
#ifdef DEBUG
        printf("READ REQ EVT\n");
#endif
        if (p_event->data.buf_req && gda) {
            if (WRLEN > 0) {
                nrfx_twis_tx_prepare(&twis, (uint8_t*)response[WRIDX], 13);
#ifdef DEBUG
                printf("INIT RESP WRIDX %d\n", WRIDX);
#endif
            }
            else {
                nrfx_twis_tx_prepare(&twis, (uint8_t*)empty, 13);
#ifdef DEBUG
                printf("INIT EMPTY\n");
#endif
            }
        }
        break;

    case NRFX_TWIS_EVT_READ_DONE:
#ifdef DEBUG
        printf("READ DONE EVT\n");
#endif
        if (WRIDX == WRLEN) {
            clear_flag();
            gda = false;
            WRIDX = 0;
#ifdef DEBUG
            printf("Sent entry %d. Cleared mutex lock!\n", WRIDX);
#endif
        }
        else WRIDX++;
#ifdef DEBUG
        printf("Data sent to master. WRIDX now at %d\n", WRIDX);
#endif
        break;

    case NRFX_TWIS_EVT_WRITE_REQ:
#ifdef DEBUG
        printf("WRITE REQ EVT\n");
#endif

        nrfx_twis_rx_prepare(&twis, (uint8_t*)RDCHAR, 1);
#ifdef DEBUG
        printf("Buffer request for write operation complete\n");
#endif
        break;

    case NRFX_TWIS_EVT_GENERAL_ERROR:
#ifdef DEBUG
        printf("ERR EVT\n");
#endif
        break;

    default:
#ifdef DEBUG
        printf("UNDEF EVT %d\n", p_event->type);
#endif
        break;
    }
}



void twis_init(void)
{
    nrfx_twis_config_t config = NRFX_TWIS_DEFAULT_CONFIG;
    config.addr[0] = SLAVE_ADDR; // Set the primary address for the TWIS device
    config.scl = SCL_PIN;
    config.sda = SDA_PIN;
    config.interrupt_priority = APP_IRQ_PRIORITY_HIGH;

    APP_ERROR_CHECK(nrfx_twis_init(&twis, &config, twis_event_handler));
    nrfx_twis_enable(&twis);
}




uint64_t murmurhash3_48(const uint8_t* key, size_t len, uint64_t seed) {
    uint64_t c1 = 0xcc9e2d51cc9e2d51;
    uint64_t c2 = 0x1b8735931b873593;
    uint64_t r1 = 47;
    uint64_t r2 = 31;
    uint64_t m = 5;
    uint64_t n = 0xe6546b64e6546b64;

    uint64_t hash = seed;
    const int nblocks = len / 8;
    const uint64_t* blocks = (const uint64_t*)(key);
    const uint8_t* tail = (const uint8_t*)(key + nblocks * 8);

    int i;
    uint64_t k;
    for (i = 0; i < nblocks; i++) {
        k = blocks[i];
        k *= c1;
        k = (k << r1) | (k >> (64 - r1));
        k *= c2;

        hash ^= k;
        hash = (hash << r2) | (hash >> (64 - r2));
        hash = hash * m + n;
    }

    k = 0;
    switch (len & 7) {
    case 7:
        k ^= (uint64_t)tail[6] << 48;
    case 6:
        k ^= (uint64_t)tail[5] << 40;
    case 5:
        k ^= (uint64_t)tail[4] << 32;
    case 4:
        k ^= (uint64_t)tail[3] << 24;
    case 3:
        k ^= (uint64_t)tail[2] << 16;
    case 2:
        k ^= (uint64_t)tail[1] << 8;
    case 1:
        k ^= (uint64_t)tail[0];
        k *= c1;
        k = (k << r1) | (k >> (64 - r1));
        k *= c2;
        hash ^= k;
    }

    hash ^= len;
    hash ^= (hash >> 33);
    hash *= 0xff51afd7ed558ccd;
    hash ^= (hash >> 33);
    hash *= 0xc4ceb9fe1a85ec53;
    hash ^= (hash >> 33);

    return hash & 0xFFFFFFFFFFFF; // Ensure it's truncated to 48 bits
}

// Function to convert the 48-bit hash to a 12-character string
void hash_to_string(uint64_t hash, char* output) {
    snprintf(output, 13, "%012llx", hash);
#ifdef DEBUG
    printf("Created hash %s\n", output);
#endif
}

// Function to hash a MAC address and return a 12-character string
void hash_mac_addr(const uint8_t mac_address[6], char* output) {
    uint64_t hash = murmurhash3_48(mac_address, 6, 0);
    hash_to_string(hash, output);
}


static ble_gap_scan_params_t m_scan_params = {
    .active = 1,                  // Active scanning set.
    .interval = SCAN_INTERVAL,      // Scan interval.
    .window = SCAN_WINDOW,        // Scan window.
    .filter_policy = BLE_GAP_SCAN_FP_ACCEPT_ALL,
    .timeout = SCAN_DURATION,      // Scan duration.
    .scan_phys = BLE_GAP_PHY_1MBPS,  // PHY used for scanning.
};

void scan_device_info_clear(void)
{
    memset(m_device, 0, sizeof(m_device));
    device_number = 0;
}



static void device_to_list_add(ble_gap_evt_adv_report_t const* p_adv_report)
{
    if (p_adv_report->rssi < threshold) {
        return;
    }

    uint8_t  idx = 0;
    uint16_t dev_name_offset = 0;
    uint16_t field_len;
    data_t   adv_data;

    adv_data.p_data = (uint8_t*)p_adv_report->data.p_data;
    adv_data.data_len = p_adv_report->data.len;

    for (idx = 0; idx < DEVICE_TO_FIND_MAX; idx++)
    {
        if (memcmp(p_adv_report->peer_addr.addr, m_device[idx].addr, sizeof(p_adv_report->peer_addr.addr)) == 0)
        {
            return;
        }
    }

    for (idx = 0; idx < DEVICE_TO_FIND_MAX; idx++)
    {
        if (!m_device[idx].is_not_empty)
        {
            device_number++;
            m_device[idx].is_not_empty = true;

            memset(m_device[idx].addr, 0, sizeof(p_adv_report->peer_addr.addr));
            memcpy(m_device[idx].addr, p_adv_report->peer_addr.addr, sizeof(p_adv_report->peer_addr.addr));

            m_device[idx].size = p_adv_report->data.len;
            memset(m_device[idx].manuf_buffer, 0, p_adv_report->data.len);
            memcpy(m_device[idx].manuf_buffer, p_adv_report->data.p_data, p_adv_report->data.len);


            m_device[idx].rssi = p_adv_report->rssi;

            return;
        }
    }
}

void assert_nrf_callback(uint16_t line_num, const uint8_t* p_file_name)
{
    app_error_handler(0xDEADBEEF, line_num, p_file_name);
}

static void leds_init(void)
{
    bsp_board_init(BSP_INIT_LEDS);
}

static void scan_start(void)
{
    ret_code_t err_code;
    err_code = nrf_ble_scan_start(&m_scan);
    APP_ERROR_CHECK(err_code);

    bsp_board_led_off(CENTRAL_CONNECTED_LED);
    bsp_board_led_on(CENTRAL_SCANNING_LED);
}

static void ble_evt_handler(ble_evt_t const* p_ble_evt, void* p_context)
{
    ret_code_t err_code;
    ble_gap_evt_t const* p_gap_evt = &p_ble_evt->evt.gap_evt;

    switch (p_ble_evt->header.evt_id)
    {
    case BLE_GAP_EVT_CONNECTED:
#ifdef DEBUG
        printf("Connected.\n");
#endif
        bsp_board_led_on(CENTRAL_CONNECTED_LED);
        bsp_board_led_off(CENTRAL_SCANNING_LED);
        break;

    case BLE_GAP_EVT_DISCONNECTED:
#ifdef DEBUG
        printf("Disconnected.\n");
#endif
        scan_start();
        break;

    case BLE_GAP_EVT_TIMEOUT:
        if (p_gap_evt->params.timeout.src == BLE_GAP_TIMEOUT_SRC_CONN)
        {
#ifdef DEBUG
            printf("Connection request timed out.\n");
#endif
        }
        break;

    case BLE_GAP_EVT_CONN_PARAM_UPDATE_REQUEST:
        err_code = sd_ble_gap_conn_param_update(p_gap_evt->conn_handle, &p_gap_evt->params.conn_param_update_request.conn_params);
        APP_ERROR_CHECK(err_code);
        break;

    case BLE_GAP_EVT_PHY_UPDATE_REQUEST:
#ifdef DEBUG
        printf("PHY update request.\n");
#endif
        ble_gap_phys_t const phys = { .rx_phys = BLE_GAP_PHY_AUTO, .tx_phys = BLE_GAP_PHY_AUTO };
        err_code = sd_ble_gap_phy_update(p_ble_evt->evt.gap_evt.conn_handle, &phys);
        APP_ERROR_CHECK(err_code);
        break;

    case BLE_GATTC_EVT_TIMEOUT:
#ifdef DEBUG
        printf("GATT Client Timeout.\n");
#endif
        err_code = sd_ble_gap_disconnect(p_ble_evt->evt.gattc_evt.conn_handle, BLE_HCI_REMOTE_USER_TERMINATED_CONNECTION);
        APP_ERROR_CHECK(err_code);
        break;

    case BLE_GATTS_EVT_TIMEOUT:
#ifdef DEBUG
        printf("GATT Server Timeout.\n");
#endif
        err_code = sd_ble_gap_disconnect(p_ble_evt->evt.gatts_evt.conn_handle, BLE_HCI_REMOTE_USER_TERMINATED_CONNECTION);
        APP_ERROR_CHECK(err_code);
        break;

    default:
        break;
    }
}

static void ble_stack_init(void)
{
    ret_code_t err_code;
    err_code = nrf_sdh_enable_request();
    APP_ERROR_CHECK(err_code);

    uint32_t ram_start = 0;
    err_code = nrf_sdh_ble_default_cfg_set(APP_BLE_CONN_CFG_TAG, &ram_start);
    APP_ERROR_CHECK(err_code);

    err_code = nrf_sdh_ble_enable(&ram_start);
    APP_ERROR_CHECK(err_code);

    NRF_SDH_BLE_OBSERVER(m_ble_observer, APP_BLE_OBSERVER_PRIO, ble_evt_handler, NULL);
}

static void button_event_handler(uint8_t pin_no, uint8_t button_action)
{
    switch (pin_no)
    {
    case LEDBUTTON_BUTTON_PIN:
        break;

    default:
        break;
    }
}

static void copy_info(void) {
    uint32_t expected = 0;
    while (!nrf_atomic_u32_cmp_exch(&response_mutex_flag, &expected, 1)) {
        expected = 0;
    }
#ifdef DEBUG
    printf("Preparing copy, set mutex lock!\n");
#endif

    memset(response, 0, sizeof(response));
    uint32_t idx = 0;
    for (; idx < device_number && idx < DEVICE_TO_FIND_MAX; idx++) {
        char hash_string[13];
        hash_mac_addr(m_device[idx].addr, hash_string);
        // Copy hash_string to response buffer
        memcpy(&response[idx], hash_string, 12);
        response[idx][12] = '\n';
    }
    response[idx][12] = '\0';

    uint32_t len;
    int l = 0;
    for (; response[l][0] != 0; l++);
#ifdef DEBUG
    printf("LEN:%d, cpidx:%d", l, idx);
#endif
    WRLEN = idx < len ? idx : len;
    clear_flag();
    scan_device_info_clear();
#ifdef DEBUG
    printf("End copy, free mutex lock!\n");
#endif
}


static void scan_evt_handler(scan_evt_t const* p_scan_evt)
{
    switch (p_scan_evt->scan_evt_id)
    {
    case NRF_BLE_SCAN_EVT_CONNECTING_ERROR:
        APP_ERROR_CHECK(p_scan_evt->params.connecting_err.err_code);
        break;

    case NRF_BLE_SCAN_EVT_NOT_FOUND:
        device_to_list_add(p_scan_evt->params.p_not_found);
        break;

    case NRF_BLE_SCAN_EVT_FILTER_MATCH:
        device_to_list_add(p_scan_evt->params.filter_match.p_adv_report);
        break;

    case NRF_BLE_SCAN_EVT_SCAN_TIMEOUT:
#ifdef DEBUG
        printf("Scan ended. Restarting scan...\n");
#endif
        copy_info();
        scan_start();
        break;

    default:
        break;
    }
}


static void buttons_init(void)
{
    ret_code_t err_code;
    static app_button_cfg_t buttons[] = { {LEDBUTTON_BUTTON_PIN, false, BUTTON_PULL, button_event_handler} };
    err_code = app_button_init(buttons, ARRAY_SIZE(buttons), BUTTON_DETECTION_DELAY);
    APP_ERROR_CHECK(err_code);
}


static void power_management_init(void)
{
    ret_code_t err_code = nrf_pwr_mgmt_init();
    APP_ERROR_CHECK(err_code);
}

static void scan_init(void)
{
    ret_code_t          err_code;
    nrf_ble_scan_init_t init_scan;

    memset(&init_scan, 0, sizeof(init_scan));

    init_scan.p_scan_param = &m_scan_params; // Set the scan parameters.
    init_scan.connect_if_match = false;
    init_scan.p_conn_param = NULL;
    init_scan.conn_cfg_tag = APP_BLE_CONN_CFG_TAG;

    err_code = nrf_ble_scan_init(&m_scan, &init_scan, scan_evt_handler);
    APP_ERROR_CHECK(err_code);
}

static void gatt_init(void)
{
    ret_code_t err_code = nrf_ble_gatt_init(&m_gatt, NULL);
    APP_ERROR_CHECK(err_code);
}

static void idle_state_handle(void)
{
    nrf_pwr_mgmt_run();
}

int main(void)
{
    leds_init();
    buttons_init();
    power_management_init();
    ble_stack_init();
    scan_init();
    gatt_init();
    twis_init();

    uint8_t mac_address[6] = { 0x1A, 0x4D, 0x16, 0x18, 0x0A, 0x9C };

    char hash_string[13];
    hash_mac_addr(mac_address, hash_string);

#ifdef DEBUG
    printf("Hash(final): %s\n", hash_string);
#endif



#ifdef DEBUG
    printf("BLE scanner example started, will print the number of devices found after %d seconds\n", SCAN_DURATION / 1000);
#endif
    scan_start();
    bsp_board_led_on(CENTRAL_SCANNING_LED);
#ifdef DEBUG
    printf("BEGIN SCANNING!\n");
#endif

    for (;;)
    {
        idle_state_handle();
    }
}

void app_error_handler(uint32_t error_code, uint32_t line_num, const uint8_t* p_file_name)
{
#ifdef DEBUG
    printf("Error Code: %lu, File: %s, Line: %lu\n", error_code, p_file_name, line_num);
#endif
    while (true) { __WFE(); }
}

void app_error_fault_handler(uint32_t id, uint32_t pc, uint32_t info)
{
#ifdef DEBUG
    printf("Fault ID: %lu, PC: %lu, Info: %lu\n", id, pc, info);
#endif
    while (true) { __WFE(); }
}
