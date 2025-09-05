FROM amazoncorretto:21
WORKDIR /app
COPY build/libs/api-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]