# ----------------------------
# Step 1: Build Stage
# ----------------------------
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy only pom.xml first (for dependency caching)
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy the full source code
COPY src ./src

# Clean and build the project (skip tests to speed up image build)
RUN mvn clean package -DskipTests

# ----------------------------
# Step 2: Runtime Stage
# ----------------------------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the packaged JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose your application's port
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
