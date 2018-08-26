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
    delay(60000);

    delay (50); 
    Serial.print("Read: ");
    Serial.print(analogRead(SENSOR_PIN));
    Serial.println();
}
