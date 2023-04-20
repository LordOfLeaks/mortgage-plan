#
# Build stage
#
FROM maven:3.9.1-eclipse-temurin-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM gcr.io/distroless/java17-debian11
COPY --from=build /home/app/target/mortgage-plan-*.jar /usr/local/lib/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]