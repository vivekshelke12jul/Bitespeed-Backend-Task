# =================================
# build stage
# ---------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder
LABEL authors="vivek"

WORKDIR /app

COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle
COPY src /app/src/

RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# =================================
# run stage
# ---------------------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]