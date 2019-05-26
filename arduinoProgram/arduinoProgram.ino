int naprej = 4;
int nazaj = 6;
int prestava = 5;
int zavora = 7;
int senzor = A5;
int senzorvalue = 0;

void setup() {
  
  Serial1.begin(9600);
  
  pinMode(naprej, OUTPUT);
  pinMode(nazaj, OUTPUT);
  pinMode(prestava, OUTPUT);
  pinMode(zavora, INPUT);

  digitalWrite(naprej, HIGH);
  digitalWrite(nazaj, HIGH);
  digitalWrite(prestava, HIGH);


}

int naprejVklopljen = 0;
int nazajVklopljen = 0;
int prestavaVKL = 0;


void loop() {
  
  
  int buttonState = digitalRead(zavora);
   senzorvalue = analogRead(senzor);
  Serial1.println(senzorvalue);

  if(senzorvalue > 800 && prestavaVKL == 0){
    digitalWrite(prestava, LOW);
    prestavaVKL = 1;
  }
  else if(senzorvalue < 300 && prestavaVKL == 1){
    digitalWrite(prestava, HIGH);
    prestavaVKL = 0;
  }
  
  if(Serial1.available() > 0){
      char data;
      data = Serial1.read();
      //Serial1.write((int)data);
      
      if(data == '1'){
        if(nazajVklopljen == 1){
          digitalWrite(nazaj, HIGH);
          nazajVklopljen = 0;
        }
        else{
          digitalWrite(naprej, LOW);
          naprejVklopljen = 1;
        }
        
      }
      else if(data == '2'){
        if(naprejVklopljen == 1){
          digitalWrite(naprej, HIGH);
          naprejVklopljen = 0;
        }
        else{
          digitalWrite(nazaj, LOW);
          nazajVklopljen = 1;
        }
      }
      else if(data == '3'){
        if(prestavaVKL == 0){
          digitalWrite(prestava, LOW);
          prestavaVKL = 1;
        }
        else{
          digitalWrite(prestava, HIGH);
          prestavaVKL=0;
        }
      }
 
  }
  
  if(buttonState == 1){
    if(naprejVklopljen == 1){
      digitalWrite(naprej, HIGH);
      prestavaVKL=0;
    }
    else if(nazajVklopljen == 1){
      digitalWrite(nazaj, HIGH);
      prestavaVKL=0;
    }
  }
  else{
    if(naprejVklopljen == 1){
      digitalWrite(naprej, LOW);
    }
    else if(nazajVklopljen == 1){
      digitalWrite(nazaj, LOW);
    }
  }
  delay(300);
}
