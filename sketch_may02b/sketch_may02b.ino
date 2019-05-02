#include <Adafruit_NeoPixel.h>
#define PIN 5 //PWM pin number
#define LEDS 60 //number of LEDs
#define SECTIONS 10 //number of sections
#define LEDS_PER_SECTION 3 //LEDs per section
#define DELAY 10 //delay for performance reasons
//strip configuration
Adafruit_NeoPixel strip = Adafruit_NeoPixel(LEDS, PIN, NEO_GRB + NEO_KHZ800);
//color arrays
int r[SECTIONS];
int g[SECTIONS];
int b[SECTIONS];
void setup() {
  Serial.begin(9600);
  strip.begin();
  //reduce the brightness if You don't have
  //additional power supply
  strip.setBrightness(128);
  strip.show();
}
void loop() {
  //read the data from serial port
  if(Serial.available() > 30) {
    if(Serial.read() == 0xff) {
      for(int i = 0; i<SECTIONS; i++) {
        r[i] = Serial.read();
        g[i] = Serial.read();
        b[i] = Serial.read();
      }
    }
  }
  //light up next sections
  for(int i=0; i<SECTIONS; i++) {
    strip.setPixelColor(i*LEDS_PER_SECTION, r[i], g[i], b[i]);
    strip.setPixelColor(i*LEDS_PER_SECTION+1, r[i], g[i], b[i]);
    strip.setPixelColor(i*LEDS_PER_SECTION+2, r[i], g[i], b[i]);
  }
  strip.show();
  delay(DELAY);
}
