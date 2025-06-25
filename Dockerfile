FROM gradle:latest AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon --stacktrace --info --console=plain --refresh-dependencies -x test

FROM openjdk:21-jdk-oracle
ARG APP_DIR=app
WORKDIR /$APP_DIR
COPY --from=build /app/build/libs/*.jar jedi-planner.jar
ENV PROFILE_MODE=prod
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "sleep 10 && java -Dspring.profiles.active=$PROFILE_MODE -jar jedi-planner.jar"]