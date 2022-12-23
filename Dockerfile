FROM maven:3.8.6-amazoncorretto-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

FROM openjdk:17
COPY --from=build /home/app/target/runflutterrun-0.0.1-SNAPSHOT.jar /usr/local/lib/runflutterrun.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/runflutterrun.jar"]