FROM openjdk:11.0.6-jre-slim

VOLUME /tmp

ADD build/libs/app-1.0.jar /app.jar
EXPOSE 7001

ENTRYPOINT ["java","-XX:MaxRAMPercentage=62.5", "-jar","/app.jar"]
