FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn -B package -DskipTests
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system househunt && useradd --system --gid househunt househunt
COPY --from=build /app/target/househunt-1.0.0.jar app.jar
USER househunt
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]