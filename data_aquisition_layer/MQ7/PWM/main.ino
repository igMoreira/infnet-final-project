// use first channel of 16 channels (started from zero)
#define LEDC_CHANNEL_0 0
// // use 13 bit precission for LEDC timer
// #define LEDC_TIMER_13_BIT 13
// // use 5000 Hz as a LEDC base frequency
// #define LEDC_BASE_FREQ 5000
// // fade LED PIN (replace with LED_BUILTIN constant for built-in LED)
#define LED_PIN 5

int brightness = 0; // how bright the LED is

void setup()
{
    Serial.begin(115200);
    // Setup timer and attach timer to a led pin
    ledcSetup(LEDC_CHANNEL_0, 5000, 8);
    ledcAttachPin(LED_PIN, LEDC_CHANNEL_0);
    delay(5000);
}

void loop()
{
    for (brightness = 0; brightness < 255; brightness += 5)
    {
        ledcWrite(LEDC_CHANNEL_0, brightness);
        delay(500);
    }
    for (brightness = 255; brightness > 0; brightness -= 5)
    {
        ledcWrite(LEDC_CHANNEL_0, brightness);
        delay(500);
    }
}
