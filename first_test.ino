#define REDPIN 5
#define GREENPIN 6
#define BLUEPIN 3
 
#define FADESPEED 5

void setup() 
{
    //Start the serial port at 9600 baud rate.
    Serial.begin(9600);
    //Set pin 13 for output.
    pinMode(REDPIN, OUTPUT);
    pinMode(GREENPIN, OUTPUT);
    pinMode(BLUEPIN, OUTPUT);
}

void loop() 
{
  //Assing the input value to a variable.
  char input;

  //Read in the characters being sent from Java.
  input = Serial.read();

  //bool on = false;
  //Turn on the LED.
  if(input == '1')
  {
    int r, g, b;
  
    // fade from blue to violet
    for (r = 0; r < 256; r++) { 
      analogWrite(REDPIN, r);
      delay(FADESPEED);
    } 
    // fade from violet to red
    for (b = 255; b > 0; b--) { 
      analogWrite(BLUEPIN, b);
      delay(FADESPEED);
    } 
    // fade from red to yellow
    for (g = 0; g < 256; g++) { 
      analogWrite(GREENPIN, g);
      delay(FADESPEED);
    } 
    // fade from yellow to green
    for (r = 255; r > 0; r--) { 
      analogWrite(REDPIN, r);
      delay(FADESPEED);
    } 
    // fade from green to teal
    for (b = 0; b < 256; b++) { 
      analogWrite(BLUEPIN, b);
      delay(FADESPEED);
    } 
    // fade from teal to blue
    for (g = 255; g > 0; g--) { 
      analogWrite(GREENPIN, g);
      delay(FADESPEED);
    } 
  }
  

  //Turn off the LED.
  if(input == '2')
  {
    analogWrite(REDPIN, 0);
    analogWrite(GREENPIN, 0);
    analogWrite(BLUEPIN, 0);
  }
}
