FROM openjdk:17-jdk-oracle
ARG APP_DIR=app
WORKDIR /$APP_DIR
COPY build/libs/*.jar jedi-planner.jar
ENV PROFILE_MODE=prod
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=$PROFILE_MODE -jar jedi-planner.jar"]