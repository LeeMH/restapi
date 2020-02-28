FROM openjdk:8-jdk-alpine

ADD ./target/api-0.0.1-SNAPSHOT.jar api.jar

EXPOSE 80

CMD ["sh", "-c", "java -Dspring.profiles.active=$ENV -jar ./api.jar"]
