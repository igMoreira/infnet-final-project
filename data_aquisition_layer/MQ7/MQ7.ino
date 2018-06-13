#define SENSOR_PIN 15


void setup() {
  pinMode(SENSOR_PIN, INPUT);  
  Serial.begin(9600);
}

void loop() {
    int sensorValue = analogRead(SENSOR_PIN);

    float sensor_voltage = sensorValue * ( 5.0 / 1024.0 );
    float RS_gas = (5.0 - sensor_voltage) / sensor_voltage;
    float ppm = (19.32 * pow(RS_gas, -0.64));
    
    Serial.println(ppm);
    delay(1000);
}
