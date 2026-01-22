FROM maven:4.0.0-rc-4-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY src ./src
COPY .cfg ./.cfg
COPY pom.xml .
RUN mvn clean package -DskipTests=true

FROM ubuntu/jre:21-24.04_stable
WORKDIR /app
COPY --from=builder /app/target/*.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
#docker build -t jru5:latest .
