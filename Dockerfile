FROM maven:3.9.4-eclipse-temurin-17-alpine AS builder

WORKDIR /build

COPY pom.xml /build/
RUN mvn dependency:go-offline

COPY src /build/src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=builder /build/target/sch-auth-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8082

CMD ["java", "-jar", "/app/app.jar"]