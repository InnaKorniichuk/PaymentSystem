FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -pl notification-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/notification-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]