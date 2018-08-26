// https://github.com/R2D2-2017/R2D2-2017/wiki/MQ-7-gas-sensor
#define LEDC_CHANNEL_0 0
#define PWM_PIN 5
#define SENSOR_PIN 32 // ADC CH 4 - 12 bits
#define ADC_RESOLUTION 4095.0
#define VOLTAGE 5.0

const float K = VOLTAGE / 5.0; // 5.66 was read from the multimeter
const int HIGH_VOLT = K * 255;
const int LOW_VOLT = K * 255 * (1.4 / VOLTAGE);

float sensor_volt;
float RS_air; //  Get the value of RS via in a clear air
float R0;     // Get the value of R0 via in H2
float sensorValue;

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
    /*--- Get a average data by testing 100 times ---*/
    //Heather 5v 60 s
    Serial.println("Heather at 5v for 60s");
    pwm_write(HIGH_VOLT);
    delay(60000);

    //Heather 1.4 V 90 s
    Serial.println("Heather at 1.4v for 90s");
    pwm_write(LOW_VOLT);
    for (int i = 0; i < 100; i++)
    {
        sensorValue = sensorValue + analogRead(SENSOR_PIN);
        delay(900);
    }

    sensorValue = sensorValue / 100.0;
    /*-----------------------------------------------*/

    sensor_volt = sensorValue / ADC_RESOLUTION * VOLTAGE;
    RS_air = (VOLTAGE - sensor_volt) / sensor_volt;
    R0 = RS_air / (26 + (1 / 3));

    Serial.print("R0 = ");
    Serial.println(R0);
    delay(1000);
}
