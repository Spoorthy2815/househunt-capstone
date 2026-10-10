FROM node:22-alpine AS frontend-build
WORKDIR /web
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build
FROM maven:3.9-eclipse-temurin-21 AS backend-build
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
COPY --from=frontend-build /web/dist ./src/main/resources/static
RUN mvn -B -ntp package -DskipTests
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system househunt && useradd --system --gid househunt househunt
COPY --from=backend-build --chown=househunt:househunt /app/target/househunt-1.0.0.jar /app/app.jar
ENV SPRING_PROFILES_ACTIVE=mysql
ENV SEED_DEMO=false
USER househunt
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]