#include <LiquidCrystal.h>
#include <Wire.h>

#define ADDRESS 0x60

const int buttonPin = 13, weight = 24; 
const char sclPin = A5, sdaPin = A4;
int score, buttonState;
char x;
LiquidCrystal lcd(12, 11, 5, 4, 3, 2);

void setup() {

  score = 0;
  buttonState = 0;

  pinMode(buttonPin, INPUT);
  pinMode(sclPin, INPUT);
  pinMode(sdaPin, INPUT);

  digitalWrite(sclPin, LOW);
  digitalWrite(sdaPin, LOW);
  digitalWrite(buttonPin, HIGH);  

  lcd.begin(16, 2);   
  lcd.setCursor(0, 0);
  lcd.print("SCORE: 0");  

  Serial.begin(115200); 
}

void loop(){  

  sendGyro();
  checkButton();
  showScore();

  delay(25);
}

void checkButton(){

  buttonState = digitalRead(buttonPin);  
  if (buttonState == HIGH){
    Serial.println(255);  

  }
}

void showScore(){

  if (Serial.available()) {
    score = Serial.read();   
    lcd.setCursor(7, 0);
    lcd.print(score); 
  }  
}

void sendGyro(){

  Wire.begin();  
  Wire.beginTransmission(ADDRESS);         
  Wire.write(5);                             
  Wire.endTransmission();                   

  Wire.requestFrom(ADDRESS, 1);              
  while(Wire.available() < 1);
  x = Wire.read();
  int value = x/8;
  Serial.println(value);  
}

