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

#define CENTRAL_SCANNING_LED            BSP_BOARD_LED_0
#define CENTRAL_CONNECTED_LED           BSP_BOARD_LED_1
#define LEDBUTTON_LED                   BSP_BOARD_LED_2

#define SCAN_INTERVAL                   0x00A0  // 100 ms
#define SCAN_WINDOW                     0x0050  // 50 ms
#define SCAN_DURATION                   0x0000  // Scanning indefinitely

#define MIN_CONNECTION_INTERVAL         MSEC_TO_UNITS(7.5, UNIT_1_25_MS)
#define MAX_CONNECTION_INTERVAL         MSEC_TO_UNITS(30, UNIT_1_25_MS)
#define SLAVE_LATENCY                   0
#define SUPERVISION_TIMEOUT             MSEC_TO_UNITS(4000, UNIT_10_MS)

#define LEDBUTTON_BUTTON_PIN            BSP_BUTTON_0
#define BUTTON_DETECTION_DELAY          APP_TIMER_TICKS(50)

#define APP_BLE_CONN_CFG_TAG            1
#define APP_BLE_OBSERVER_PRIO           3

NRF_BLE_SCAN_DEF(m_scan);
BLE_LBS_C_DEF(m_ble_lbs_c);
NRF_BLE_GATT_DEF(m_gatt);
BLE_DB_DISCOVERY_DEF(m_db_disc);

#define SCAN_LIST_REFRESH_INTERVAL 10000
#define FOUND_DEVICE_REFRESH_TIME APP_TIMER_TICKS(SCAN_LIST_REFRESH_INTERVAL)

#define DEVICE_NAME_MAX_SIZE 20
#define DEVICE_TO_FIND_MAX 50

typedef struct
{
    bool      is_not_empty;
    uint16_t  size;
    uint8_t   addr[BLE_GAP_ADDR_LEN];
    char      dev_name[DEVICE_NAME_MAX_SIZE];
    uint8_t   manuf_buffer[BLE_GAP_ADV_SET_DATA_SIZE_MAX];
    int8_t    rssi;
} scanned_device_t;

static uint32_t device_number = 0;
static scanned_device_t m_device[DEVICE_TO_FIND_MAX];

static ble_gap_scan_params_t m_scan_params = {
    .active        = 1,                  // Active scanning set.
    .interval      = SCAN_INTERVAL,      // Scan interval.
    .window        = SCAN_WINDOW,        // Scan window.
    .filter_policy = BLE_GAP_SCAN_FP_ACCEPT_ALL,
    .timeout       = SCAN_DURATION,      // Scan duration.
    .scan_phys     = BLE_GAP_PHY_1MBPS,  // PHY used for scanning.
};

void scan_device_info_clear(void)
{
    memset(m_device, 0, sizeof(m_device));
    device_number = 0;
}

void print_adv_data(uint8_t *data, uint16_t length)
{
    for (uint16_t i = 0; i < length; i++)
    {
        printf("%X  ", data[i]);
    }
    printf("\n");
}

void device_list_print(scanned_device_t * p_device)
{
    printf("\n\n--------------------------------------------------------\n");
    printf("--------------------------------------------------------\n");
    printf("Devices found: %d\n", device_number);
    for (uint8_t i = 0; i < DEVICE_TO_FIND_MAX; i++)
    {
        if (p_device[i].is_not_empty)
        {
            printf("Device %d:\n", i + 1);
            printf("  Address: %X:%X:%X:%X:%X:%X\n",
                   p_device[i].addr[0], p_device[i].addr[1], p_device[i].addr[2],
                   p_device[i].addr[3], p_device[i].addr[4], p_device[i].addr[5]);
            printf("  Name: %s\n", p_device[i].dev_name);
            printf("  RSSI: %d dBm\n", p_device[i].rssi);
        }
    }
    printf("--------------------------------------------------------\n");
    scan_device_info_clear();
}

scanned_device_t * scan_device_info_get(void)
{
    return m_device;
}

typedef struct
{
    uint8_t * p_data;
    uint16_t  data_len;
} data_t;

static void device_to_list_add(ble_gap_evt_adv_report_t const * p_adv_report)
{
    uint8_t  idx             = 0;
    uint16_t dev_name_offset = 0;
    uint16_t field_len;
    data_t   adv_data;

    adv_data.p_data   = (uint8_t *)p_adv_report->data.p_data;
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

            // Parse and print the device name
            if (ble_advdata_name_find(adv_data.p_data, adv_data.data_len, m_device[idx].dev_name))
            {
                printf("Device Name: %s\n", m_device[idx].dev_name);
            }
            else if (ble_advdata_short_name_find(adv_data.p_data, adv_data.data_len, m_device[idx].dev_name, 3))
            {
                printf("Short Device Name: %s\n", m_device[idx].dev_name);
            }
            else
            {
                strcpy(m_device[idx].dev_name, "Unknown");
                printf("Device Name: Unknown\n");
            }

            // Parse and print the appearance if available
            uint16_t appearance;
            if (ble_advdata_appearance_find(adv_data.p_data, adv_data.data_len, &appearance))
            {
                printf("Appearance: %d\n", appearance);
            }

            // Parse and print the UUID if available
            ble_uuid_t target_uuid = { .uuid = BLE_UUID_DEVICE_INFORMATION_SERVICE, .type = BLE_UUID_TYPE_BLE };
            if (ble_advdata_uuid_find(adv_data.p_data, adv_data.data_len, &target_uuid))
            {
                printf("UUID: Device Information Service\n");
            }

            m_device[idx].rssi = p_adv_report->rssi;
            printf("RSSI: %d dBm\n", m_device[idx].rssi);

            // Print the advertising packet data as characters with two spaces between them
            printf("Advertising Data: ");
            for (uint16_t i = 0; i < adv_data.data_len; i++)
            {
                    printf("%X  ", adv_data.p_data[i]);
            }
            printf("\n");

            return;
        }
    }
}

void assert_nrf_callback(uint16_t line_num, const uint8_t * p_file_name)
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

static void ble_evt_handler(ble_evt_t const * p_ble_evt, void * p_context)
{
    ret_code_t err_code;
    ble_gap_evt_t const * p_gap_evt = &p_ble_evt->evt.gap_evt;

    switch (p_ble_evt->header.evt_id)
    {
        case BLE_GAP_EVT_CONNECTED:
            printf("Connected.\n");
            bsp_board_led_on(CENTRAL_CONNECTED_LED);
            bsp_board_led_off(CENTRAL_SCANNING_LED);
            break;

        case BLE_GAP_EVT_DISCONNECTED:
            printf("Disconnected.\n");
            scan_start();
            break;

        case BLE_GAP_EVT_TIMEOUT:
            if (p_gap_evt->params.timeout.src == BLE_GAP_TIMEOUT_SRC_CONN)
            {
                printf("Connection request timed out.\n");
            }
            break;

        case BLE_GAP_EVT_CONN_PARAM_UPDATE_REQUEST:
            err_code = sd_ble_gap_conn_param_update(p_gap_evt->conn_handle, &p_gap_evt->params.conn_param_update_request.conn_params);
            APP_ERROR_CHECK(err_code);
            break;

        case BLE_GAP_EVT_PHY_UPDATE_REQUEST:
            printf("PHY update request.\n");
            ble_gap_phys_t const phys = {.rx_phys = BLE_GAP_PHY_AUTO, .tx_phys = BLE_GAP_PHY_AUTO};
            err_code = sd_ble_gap_phy_update(p_ble_evt->evt.gap_evt.conn_handle, &phys);
            APP_ERROR_CHECK(err_code);
            break;

        case BLE_GATTC_EVT_TIMEOUT:
            printf("GATT Client Timeout.\n");
            err_code = sd_ble_gap_disconnect(p_ble_evt->evt.gattc_evt.conn_handle, BLE_HCI_REMOTE_USER_TERMINATED_CONNECTION);
            APP_ERROR_CHECK(err_code);
            break;

        case BLE_GATTS_EVT_TIMEOUT:
            printf("GATT Server Timeout.\n");
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

static void scan_evt_handler(scan_evt_t const * p_scan_evt)
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
            // Handle scan timeout event
            break;

        default:
            break;
    }
}

static void buttons_init(void)
{
    ret_code_t err_code;
    static app_button_cfg_t buttons[] = {{LEDBUTTON_BUTTON_PIN, false, BUTTON_PULL, button_event_handler}};
    err_code = app_button_init(buttons, ARRAY_SIZE(buttons), BUTTON_DETECTION_DELAY);
    APP_ERROR_CHECK(err_code);
}

static void log_init(void)
{
}

static void adv_list_timer_handle(void * p_context)
{
    scanned_device_t * p_device_list = scan_device_info_get();
    device_list_print(p_device_list);
}

static void timer_init(void)
{
    ret_code_t err_code = app_timer_init();
    APP_ERROR_CHECK(err_code);

    APP_TIMER_DEF(adv_list_timer);
    err_code = app_timer_create(&adv_list_timer, APP_TIMER_MODE_REPEATED, adv_list_timer_handle);
    APP_ERROR_CHECK(err_code);

    err_code = app_timer_start(adv_list_timer, FOUND_DEVICE_REFRESH_TIME, NULL);
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

    init_scan.p_scan_param      = &m_scan_params; // Set the scan parameters.
    init_scan.connect_if_match  = false;
    init_scan.p_conn_param      = NULL;
    init_scan.conn_cfg_tag      = APP_BLE_CONN_CFG_TAG;

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
    log_init();
    timer_init();
    leds_init();
    buttons_init();
    power_management_init();
    ble_stack_init();
    scan_init();
    gatt_init();

    printf("BLE scanner example started, will print the number of devices found after %d seconds\n", SCAN_LIST_REFRESH_INTERVAL / 1000);
    scan_start();
    bsp_board_led_on(CENTRAL_SCANNING_LED);
    printf("BEGIN SCANNING!\n");

    for (;;)
    {
        idle_state_handle();
    }
}

void app_error_handler(uint32_t error_code, uint32_t line_num, const uint8_t * p_file_name)
{
    printf("Error Code: %lu, File: %s, Line: %lu\n", error_code, p_file_name, line_num);
    while (true) { __WFE(); }
}

void app_error_fault_handler(uint32_t id, uint32_t pc, uint32_t info)
{
    printf("Fault ID: %lu, PC: %lu, Info: %lu\n", id, pc, info);
    while (true) { __WFE(); }
}
