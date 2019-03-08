# 개요
연봉 계산기 이다. 안드로이드 어플 이다. 

# 주의 사항
## sqlite.jar 파일 문제
모듈에서 sqlite jar 라이브러리 를 로드하지 못하는 상황.

임시적으로, JDK 자체에 라이브러리를 넣고 구동하면 해결이 된다.

C:\Program Files\Java\jdk1.8.0_66\jre\lib\ext 에 넣어주면 된다.

sqlite-jdbc-3.20.0.jar 파일을 넣어주면 된다.

다운로드 주소 : https://github.com/xerial/sqlite-jdbc

# TODO
* UI 의 편리성을 좀 더 고민해 볼 것.
* Master Calculator 구현.
** 각 보험을 제외 선택 가능.
** 각 보험에서 수령액을 직접 입력 가능
** 한달 28~31/365 로 환산.
* 입력금액 한글 표시 기능. (아직까지는 딱히 필요성을 못 느끼는 중)
* 지역가입자 건강보험 처리. 어떻게 할지 고민 중.
* 설정 변경시 표시됨
