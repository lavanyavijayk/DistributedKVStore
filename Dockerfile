# Use an appropriate base image that supports Java 23
FROM openjdk:23-ea-jdk-slim AS build

# Install Maven
RUN apt-get update && apt-get install -y maven && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /DistributedKVStore
COPY ./ .

# Copy the pom.xml and resolve dependencies
RUN mvn dependency:resolve dependency:resolve-plugins

# Build the application
RUN mvn clean package -DskipTests

FROM openjdk:23-ea-jdk-slim

# Expose the necessary ports
EXPOSE 5001 5002 5003


# Copy the built jar from the build stage 
# <----Change the jar file path---->
COPY --from=build /target/DistributedKVStore-1.0-SNAPSHOT.jar app.jar
