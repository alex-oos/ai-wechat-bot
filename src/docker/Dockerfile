FROM openjdk:11-jdk-alpine
VOLUME /tmp
ADD target/wechat-bot.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 9919