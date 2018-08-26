#include <SimpleKalmanFilter.h>

#define SENSOR_PIN 32 // ADC CH 4 - 12 bits
#define READINGS 60


SimpleKalmanFilter simpleKalmanFilter(80, 80, 0.01);
const float sensor_volt_factor = 5.0 / 4095.0;

float calc_ppm(int sensorValue)
{
  float sensor_voltage = sensorValue * (5.0 / 4095.0 /*1024.0*/);
  float RS_gas = (5.0 - sensor_voltage) / sensor_voltage;
  float ppm = (19.32 * pow(RS_gas, -0.64));
  return ppm;
}

void setup()
{
  Serial.begin(115200);

  delay(5000);
  for (int i = 0; i < READINGS; i++)
  {
    int sensor_reading = analogRead(SENSOR_PIN);
    int estimated_value = (int) simpleKalmanFilter.updateEstimate(sensor_reading);

    float ppm = calc_ppm(sensor_reading);
    float estimated_ppm = calc_ppm(estimated_value);

    Serial.print(ppm);
    Serial.print(", ");
    Serial.println(estimated_ppm);
    // Serial.println(map(sensor_reading, 0, 4095, 0, 500) / 100.0);
    delay(5000);
  }
  Serial.println();
}

void loop()
{
    Serial.print(".");
    delay(5000);
}

