FROM openjdk:8-jre
ADD target/wallet.jar /
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /wallet.jar" ]
