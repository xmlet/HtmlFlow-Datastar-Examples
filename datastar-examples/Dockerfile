# ---------- Build stage ----------
FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

# Copy Gradle wrapper first for better layer caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle* settings.gradle* ./

RUN chmod +x gradlew

# Download Gradle dependencies
RUN ./gradlew dependencies --no-daemon || true

# Copy the rest of the project
COPY . .

# Build the application
RUN ./gradlew clean installDist --no-daemon

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/build/install/workspace/lib/ ./

ENV SERVER_TYPE=Ktor

ENTRYPOINT ["java", "-cp", "/app/*", "pt.isel.AppKt"]
