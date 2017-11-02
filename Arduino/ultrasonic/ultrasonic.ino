// 초음파센서의 송신 핀
int trig = 8;

// 초음파센서의 수신 핀
int echo = 9;

// led 연결 핀
int led = A0;


// 실행시 가장 먼저 호출되는 함수(최초 1회만 실행)
void setup() {
 // 초음파센서의 동작 상태를 확인하기 위하여 시리얼 통신을 설정(전송속도 9600bps)
 Serial.begin(9600);
 // 초음파센서의 송신부로 연결된 핀을 OUTPUT으로 설정
 pinMode(trig, OUTPUT);
 // 초음파센서의 수신부로 연결된 핀을 INPUT으로 설정
 pinMode(echo, INPUT);
// LED가 연결된 핀을 OUTPUT으로 설정합니다.
 pinMode(led, OUTPUT);
}


// setup() 함수 호출 후, loop() 함수가 호출
// 블록 안의 코드를 무한반복
void loop() { 
 int a = 0;
 // 초음파 센서는 송신부터 수신까지의 시간을 기준으로 거리를 측정
 // trig로 연결된 핀이 송신부, echo로 연결된 핀이 수신부
 digitalWrite(trig, LOW);
 digitalWrite(echo, LOW);
 delayMicroseconds(2);
 digitalWrite(trig, HIGH);
 delayMicroseconds(10);
 digitalWrite(trig, LOW);

 // 수신부의 초기 로직레벨을 HIGH로 설정하고, 반사된 초음파에 의하여 ROW 레벨로 바뀌기 전까지의 시간 측정(단위 : 마이크로초)
 unsigned long duration = pulseIn(echo, HIGH);

 // 초음파의 속도는 초당 340미터를 이동하거나, 29마이크로초 당 1센치를 이동
 // 초음파의 이동 거리 = duration(왕복에 걸린시간) / 29 / 2
 float distance = duration / 29.0 / 2.0;
 
 // Serial Monitor 출력
 Serial.print(distance);
 // 구분자
 Serial.print("a");
 Serial.print("\n");

 if (distance < 30) {
   // LED가 연결된 핀의 로직레벨을 HIGH (5V)로 설정(LED on)
   digitalWrite(led, HIGH);
 }
 else {
   // LED가 연결된 핀의 로직레벨을 LOW (0V)로 설정(LED off)
   digitalWrite(led, LOW);
 }
 // 지정한 시간동안 대기
 delay(10);
 
}
