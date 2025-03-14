FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . .

# Fix line endings in mvnw and make it executable
RUN sed -i 's/\r$//' ./mvnw && chmod +x ./mvnw

RUN ./mvnw dependency:go-offline

CMD ["./mvnw", "spring-boot:run"]