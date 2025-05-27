#include <DS18B20.h>
#include <SoftwareSerial.h>


#define LOW_ALARM 20
#define HIGH_ALARM 25

DS18B20 ds(2);
uint8_t address[] = {0x28, 0x67, 0x44, 0x46, 0xD4, 0xDA, 0x5, 0x40};
uint8_t selected;

void setup() {
  Serial.begin(9600);
  selected = ds.select(address);

  if (selected) {
    ds.setAlarms(LOW_ALARM, HIGH_ALARM);
  } else {
    Serial.println("Device not found!");
  }
}

void loop() {

  Serial.print(ds.getTempC());
  Serial.println(" C");

//en caso de testeo por serial.
  if(Serial.available()){
    char Dato = Serial.read();
    Serial.print("Dato recibido : ");
    Serial.print(Dato); 
  }

  
}
