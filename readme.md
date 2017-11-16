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
* Quick Calculate / Normal Calculator / Master Calculator 구성
* UI 의 편리성을 좀 더 고민해 볼 것.
