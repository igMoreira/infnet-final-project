#include <SimpleKalmanFilter.h>

#define SENSOR_PIN 32 // ADC CH 4 - 12 bits
#define READINGS 60

SimpleKalmanFilter kFilt1(80, 80, 0.11);
SimpleKalmanFilter kFilt2(200, 200, 0.1);

void setup()
{
    Serial.begin(115200);

    delay(5000);
    for (int i = 0; i < READINGS; i++)
    {
        int sensor_reading = analogRead(SENSOR_PIN);

        Serial.print(sensor_reading);
        Serial.print("; ");
        Serial.print(kFilt1.updateEstimate(sensor_reading));
        Serial.print("; ");
        Serial.print(kFilt2.updateEstimate(sensor_reading));
        Serial.println();
        delay(5000);
    }
    Serial.println("");
}

void loop()
{
    Serial.print(".");
    delay(5000);
}
