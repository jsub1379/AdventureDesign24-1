#include <Wire.h>

const int nrfSlaveAddress = 0x04;  // Address of the nRF device, assumed to be configured as TWI slave
char buf[1040];
uint32_t buflen = 0;
void setup() {
  Wire.begin();  // Start I2C as master
  Serial.begin(115200);  // Start serial communication for debugging
}

void loop() {
  if (Serial.available() > 0) {
    while (Serial.available() > 0) {
      char cmd = Serial.read();
      if(cmd == 'g'){
        Serial.println("GET CMD REACH");
        getData();
      }
      else if(cmd == 's'){
        Serial.println("SET CMD REACH");
        setThreshold((uint8_t)60);
      }
    }
  }

  delay(1000);  // Delay before checking serial input again
}

void setThreshold(uint8_t threshold){
  Serial.println("BS");
  Wire.beginTransmission(nrfSlaveAddress);
  Wire.write('S');
  Wire.endTransmission();
  Wire.beginTransmission(nrfSlaveAddress);
  Wire.write('T');
  Wire.endTransmission();
  Wire.beginTransmission(nrfSlaveAddress);
  Wire.write(threshold);
  Wire.endTransmission();
  Wire.beginTransmission(nrfSlaveAddress);
  Wire.write(255);
  Wire.endTransmission();
  Serial.println("ES");
}

void getData(){
  Wire.beginTransmission(nrfSlaveAddress);
  Wire.write('G');
  Wire.endTransmission();
  Wire.beginTransmission(nrfSlaveAddress);
  Wire.write('D');
  Wire.endTransmission();
  Wire.beginTransmission(nrfSlaveAddress);
  Wire.write('A');
  Wire.endTransmission();
  Wire.beginTransmission(nrfSlaveAddress);
  Wire.write(255);
  Wire.endTransmission();
  delay(300);
  uint32_t idx = 0;
  uint32_t sidx = 0;
  do {
    
    //Read each line
    Wire.requestFrom(nrfSlaveAddress, 13);
    while (Wire.available()) {
      char c = Wire.read();
      buf[idx*13 + sidx] = c;
      sidx++;
      if(sidx>=13 || c == '\n'){
        sidx=0;
        idx++;
        break;
      }
      else if(c == '\0'){
        printScanned();
        return;
      }
    }
  }while(idx < 80);
  buflen = idx;
  printScanned();
}

void printScanned(){
  Serial.println("SCANNED DATA:");
  for(uint32_t i=0; i<1040 && buf[i] != '\0'; i++){
    Serial.print(buf[i]);
  }
  Serial.println();
}
