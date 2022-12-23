FROM openjdk:17

WORKDIR /app

# Copy the jar file into our app
COPY ./target/runflutterrun-0.0.1-SNAPSHOT.jar /app

# Exposing port 8080
EXPOSE 8080

# Starting the application
CMD ["java", "-jar", "runflutterrun-0.0.1-SNAPSHOT.jar"]