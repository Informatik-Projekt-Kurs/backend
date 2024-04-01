# Build the application
FROM maven:3.8.5-openjdk-17 as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ ./src/
RUN mvn clean package -DskipTests=true

# Run the application
FROM openjdk:17
WORKDIR /app
# Create a group and user
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
COPY --from=builder /app/target/MeetMate.jar /app/MeetMate.jar
# Use the created user
USER appuser
CMD ["java", "-jar", "MeetMate.jar"]
