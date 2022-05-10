FROM registry.access.redhat.com/ubi8/openjdk-11-runtime:1.12-1.1651233103
USER root

RUN useradd appuser \
    && mkdir /java-app \
    && chown appuser /java-app

COPY /target/*.jar /java-app/app.jar
RUN chmod -R ug+xrw /java-app
USER appuser
EXPOSE 8080
WORKDIR /java-app

ENTRYPOINT ["java", "-jar", "app.jar"]
