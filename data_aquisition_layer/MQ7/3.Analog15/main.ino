#define SENSOR_PIN 15
#define READINGS 60

void setup()
{
  pinMode(SENSOR_PIN, INPUT);
  Serial.begin(9600);

  delay(5000);
  for (int i = 0; i < READINGS; i++)
  {
    Serial.println(analogRead(SENSOR_PIN));
    delay(5000);
  }
  Serial.println("");
}

void loop()
{
  Serial.print(".");
  delay(5000);
}
