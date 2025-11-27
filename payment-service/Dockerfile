FROM eclipse-temurin:17-jre-alpine

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS="-Duser.timezone=Asia/Seoul"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]