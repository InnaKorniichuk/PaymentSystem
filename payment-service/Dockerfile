FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -pl payment-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/payment-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]