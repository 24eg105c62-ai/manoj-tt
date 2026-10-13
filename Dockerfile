# ==============================================================================
# Stage 1: Build Frontend (React + Vite)
# ==============================================================================
FROM node:20-alpine AS frontend-builder
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# ==============================================================================
# Stage 2: Build Backend (Spring Boot with bundled static React assets)
# ==============================================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /app
COPY backend/pom.xml ./backend/
RUN mvn -f backend/pom.xml dependency:go-offline
COPY backend/src ./backend/src
# Copy frontend distribution files directly into Spring Boot static resources
COPY --from=frontend-builder /app/frontend/dist ./backend/src/main/resources/static
RUN mvn -f backend/pom.xml clean package -DskipTests

# ==============================================================================
# Stage 3: Production Runtime (Single Container, Single URL, Single Port)
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend-builder /app/backend/target/*.jar app.jar
ENV PORT=8081
EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
