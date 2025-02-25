# Шаг сборки (Build Stage)
FROM maven:3.8.6-amazoncorretto-17 AS build
COPY ./pom.xml /build/
WORKDIR /build
RUN mvn dependency:go-offline
COPY ./src /build/src/
# Пропускаем тесты:
RUN mvn package -DskipTests

# Шаг запуска (Run Stage)
FROM openjdk:17-jdk
ARG JAR_FILE=/build/target/*.jar
COPY --from=build $JAR_FILE /opt/news-parser/app.jar
ENTRYPOINT ["java", "-jar", "/opt/news-parser/app.jar"]