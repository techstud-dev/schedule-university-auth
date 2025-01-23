FROM maven:3.9.4-eclipse-temurin-17-alpine

WORKDIR /app

COPY pom.xml /app/

RUN mvn dependency:resolve

CMD ["mvn", "spring-boot:run"]