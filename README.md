rest api skeleton README.md
---
공통사항
1. lombok 의존성을 가지고 있으므로, lombok plugin을 반드시 설치.
2. intellij > "Settings > Build > Compiler > Annotation Processors" enable Annotation Processing 체크
3. profile은 test가 디폴트 설정이며, h2 db를 사용 -> dev,prod등 필요한 설정은 properties파일에 추가
---
주의사항
1. restful 하지 않은, post method 위주로 api를 구성합니다.
2. request와 response는 DTO 클래스안에 묶어서 관리 합니다.
---
##Build & Run
mvn -DskipTests=true clean install
docker build -t jigulsw/restapi .
docker run -e ENV=test -p 80:8883 jigulsw/restapi

