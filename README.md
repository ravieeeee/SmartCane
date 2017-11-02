## SmartCane
시각장애인용 지팡이 제작 프로젝트 (2017.7.3 ~ 2017.8.24)

## 목표
* distance의 즉각적 측정 및 통신 
* 사용자에게 2단계로 알람(소리, 소리+진동)

## FlowChart
1. arduino의 ultrasonic sensor를 이용해 물체와의 distance 측정
2. distance를 raspberry pi로 전달(java)
3. 적절한 처리 후, ios 앱(swift)으로 distance 전달
4. 위험 시 사용자에게 경고 및 알람