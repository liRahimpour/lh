FROM eclipse-temurin:21-jdk

COPY target/backend-*.jar /opt/app/app.jar

WORKDIR /opt/app

ENTRYPOINT ["java", "-jar", "app.jar"]