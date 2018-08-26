#define SENSOR_PIN 32 // ADC CH 4 - 12 bits
#define READINGS 60

void setup()
{
    Serial.begin(115200);
}

void loop()
{
    int v = analogRead(SENSOR_PIN);
    Serial.print(v);
    Serial.print(", ");
    Serial.println(map(v, 0, 4095, 0, 500) / 100.0);
    delay(1000);
}
