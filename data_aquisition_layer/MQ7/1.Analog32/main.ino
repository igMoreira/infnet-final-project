#define SENSOR_PIN 32 // ADC CH 4 - 12 bits
#define READINGS 600

void setup()
{
  Serial.begin(115200);

  delay(5000);
  for (int i = 0; i < READINGS; i++)
  {
    // Serial.println(analogRead(SENSOR_PIN));
    int v = analogRead(SENSOR_PIN);
    Serial.print(v);
    // Serial.print(", ");
    // Serial.print(map(v, 0, 4095, 0, 500) / 100.0);
    Serial.println();
    delay(5000);
  }
  Serial.println();
}

void loop()
{
  Serial.print(".");
  delay(5000);
}
