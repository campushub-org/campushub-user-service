FROM openjdk:17.0.2
VOLUME /tmp
ADD target/campushub-user-service.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]