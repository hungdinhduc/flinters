FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p /app/output

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
