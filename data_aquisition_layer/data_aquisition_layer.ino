#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <BLEUUID.h>

#define SERVICE_UUID "6e400001-b5a3-f393-e0a9-e50e24dcca9e" // UART service UUID (128 bit)
#define CHARACTERISTIC_UUID "6e400003-b5a3-f393-e0a9-e50e24dcca9e" // characteristic UUID (128 bit)
#define SENSOR_PIN 32

BLEServer *pServer = NULL;
BLEService *pService = NULL;
BLECharacteristic *pCharacteristic = NULL;
float txValue = 0;
bool deviceConnected = false;
bool oldDeviceConnected = false;

class MyServerCallbacks : public BLEServerCallbacks
{
  void onConnect(BLEServer *pServer)
  {
    Serial.println("Device connected. Stopping advertising...");
    pServer->getAdvertising()->stop();
    deviceConnected = true;
  };

  void onDisconnect(BLEServer *pServer)
  {
    deviceConnected = false;
    delay(1000);
    Serial.println("Advertising...");
    pServer->getAdvertising()->addServiceUUID(BLEUUID(SERVICE_UUID));
    pServer->getAdvertising()->start();
  }
};

void setup()
{
  Serial.begin(9600);
  /* === Create the BLE Device === 
  * Considering note from github (https://github.com/nkolban/esp32-snippets/issues/269):
  *   - "When you are advertising 128 bit UUID then name of device cant be longer than 5 characters."
  * 
  * COD stands for CO Detector or Carbon Monoxide Detector
  */ 
  BLEDevice::init("COD");

  // Create the BLE Server
  pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks());

  // Create the BLE Service
  pService = pServer->createService(SERVICE_UUID);

  // Create a BLE Characteristic
  pCharacteristic = pService->createCharacteristic(
      CHARACTERISTIC_UUID,
      BLECharacteristic::PROPERTY_READ |
      BLECharacteristic::PROPERTY_NOTIFY);

  pCharacteristic->addDescriptor(new BLE2902());

  // Start the service
  pService->start();
  
  // Start advertising
  pServer->getAdvertising()->start();
}

void loop()
{
  if (deviceConnected)
  {
    txValue = acquire_data();
    Serial.println(txValue);
    char txString[8];
    dtostrf(txValue, 1, 2, txString); // float_val, min_width, digits_after_decimal, char_buffer

    pCharacteristic->setValue(txString);

    pCharacteristic->notify(); // Send the value to the app!
    delay(5000);
  }
  else
  {
    delay(1000);
    Serial.println("Not connected.");
  }
}

float acquire_data()
{
  int sensorValue = analogRead(SENSOR_PIN);

  float sensor_voltage = sensorValue * (5.0 / 4095.0 /*1024.0*/);
  float RS_gas = (5.0 - sensor_voltage) / sensor_voltage;
  float ppm = (19.32 * pow(RS_gas, -0.64));

  return ppm;
}
