# Use an official OpenJDK runtime as a base image
FROM maven:latest AS build


# Set the working directory
WORKDIR /app

# Copy the pom.xml and source code into the container
COPY pom.xml .
COPY src/ ./src/

# Build the JAR file using Maven
RUN mvn clean package -DskipTests

# Use a smaller base image to run the application
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/efc-user-0.0.1-SNAPSHOT.jar app.jar

# Expose the default port for Spring Boot (8080)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
