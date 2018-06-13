#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>


BLECharacteristic *pCharacteristic;
bool deviceConnected = false;
float txValue = 0;

#define SERVICE_UUID           "6E400001-B5A3-F393-E0A9-E50E24DCCA9E" // UART service UUID
#define CHARACTERISTIC_UUID_TX "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"
#define SENSOR_PIN 32

class MyServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
      deviceConnected = true;
    };

    void onDisconnect(BLEServer* pServer) {
      deviceConnected = false;
    }
};

void setup() {
  Serial.begin(9600);
  // Create the BLE Device
  BLEDevice::init("ESP32 UART Test"); // Give it a name

  // Create the BLE Server
  BLEServer *pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks());

  // Create the BLE Service
  BLEService *pService = pServer->createService(SERVICE_UUID);

  // Create a BLE Characteristic
  pCharacteristic = pService->createCharacteristic(
                      CHARACTERISTIC_UUID_TX,
                      BLECharacteristic::PROPERTY_NOTIFY
                    );
                      
  pCharacteristic->addDescriptor(new BLE2902());

  // Start the service
  pService->start();

  // Start advertising
  pServer->getAdvertising()->start();
}

void loop() {
  if (deviceConnected) {
    txValue = acquire_data();
    Serial.println(txValue);
    
    char txString[8]; 
    dtostrf(txValue, 1, 2, txString); // float_val, min_width, digits_after_decimal, char_buffer    

    pCharacteristic->setValue(txString);
    
    pCharacteristic->notify(); // Send the value to the app!
  }
  delay(1000);
}

float acquire_data(){
    int sensorValue = analogRead(SENSOR_PIN);

    float sensor_voltage = sensorValue * ( 5.0 / 1024.0 );
    float RS_gas = (5.0 - sensor_voltage) / sensor_voltage;
    float ppm = (19.32 * pow(RS_gas, -0.64));
    
    return ppm;
}

