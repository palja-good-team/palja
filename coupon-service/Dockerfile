FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

COPY . .
RUN gradle clean bootJar

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "/app/app.jar"]