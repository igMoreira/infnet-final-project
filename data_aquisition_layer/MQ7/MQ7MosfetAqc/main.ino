#include <math.h>

#define LEDC_CHANNEL_0 0
#define PWM_PIN 5
#define SENSOR_PIN 32 // ADC CH 4 - 12 bits
#define ADC_RESOLUTION 4095.0
#define VOLTAGE 5.0
#define R0 0.06

const float K = VOLTAGE / 5.0; // 5.66 was read from the multimeter
const int HIGH_VOLT = K * 255;
const int LOW_VOLT = K * 255 * (1.4 / VOLTAGE);

float RS_gas = 0;
float ratio = 0;
float sensorValue = 0;

void pwm_write(int duty)
{
    ledcWrite(LEDC_CHANNEL_0, duty);
}

void setup()
{
    Serial.begin(115200);
    ledcSetup(LEDC_CHANNEL_0, 490, 8);
    ledcAttachPin(PWM_PIN, LEDC_CHANNEL_0);
}

void loop()
{

    //Heather 5v 60 s
    Serial.println("Heather at 5v for 60s");
    pwm_write(HIGH_VOLT);
    delay(60000);

    //Heather 1.4 V 90 s
    Serial.println("Heather at 1.4v for 90s");
    pwm_write(LOW_VOLT);
    for (int i = 0; i < 900; i++)
    {
        sensorValue = analogRead(SENSOR_PIN);
        int sensor_volt = sensorValue / ADC_RESOLUTION * VOLTAGE;
        RS_gas = (5.0 - sensor_volt) / sensor_volt;
        ratio = RS_gas / R0;                                   //Replace R0 with the value found using the calibration code
        float ppm = 100 * pow(log10(40) / log10(0.09), ratio); //Formula for co 2 concentration
        Serial.println(ppm);
        delay(100);
    }
}
