#define LEDC_CHANNEL_0 0
#define PWM_PIN 5

const float K = 5.0 / 5.00;
const int HIGH_VOLT = K * 255;
const int LOW_VOLT = K * 255 * (1.4 / 5.0);

void setup_pwm()
{
    ledcSetup(LEDC_CHANNEL_0, 5000, 8);
    ledcAttachPin(PWM_PIN, LEDC_CHANNEL_0);
}

void setup()
{
    Serial.begin(115200);
    ledcSetup(LEDC_CHANNEL_0, 490, 8);
    ledcAttachPin(PWM_PIN, LEDC_CHANNEL_0);
}

void loop()
{
    ledcWrite(LEDC_CHANNEL_0, 255);
    Serial.print(5);
    Serial.print(", ");
    Serial.print(255);
    Serial.println();
    delay(10000);

    ledcWrite(LEDC_CHANNEL_0, HIGH_VOLT);
    Serial.print(5);
    Serial.print(", ");
    Serial.print(HIGH_VOLT);
    Serial.println();
    delay(10000);

    ledcWrite(LEDC_CHANNEL_0, LOW_VOLT);
    Serial.print(1.4);
    Serial.print(", ");
    Serial.print(LOW_VOLT);
    Serial.println();
    delay(10000);
}
