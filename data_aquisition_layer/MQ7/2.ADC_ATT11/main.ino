#include <driver/adc.h>

#define SENSOR_PIN 32 // ADC 12 bits
#define READINGS 60

int count = 1;

void setup()
{
  Serial.begin(9600);
  adc1_config_width(ADC_WIDTH_12Bit);
  // adc1_config_channel_atten(ADC1_CHANNEL_4, ADC_ATTEN_0db);
  // adc1_config_channel_atten(ADC1_CHANNEL_4, ADC_ATTEN_11db);
  adc1_config_channel_atten(ADC1_CHANNEL_4, ADC_ATTEN_6db);
  
  delay(5000);

  for (int i = 0; i < READINGS; i++)
  {
    Serial.println(adc1_get_voltage(ADC1_CHANNEL_4));
    delay(5000);
  }
  Serial.println("");
}

void loop()
{
  Serial.print(".");
  delay(5000);
}
