# Stage 1: Build file JAR (Dùng image Java 25 mới nhất)
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw
# Build project, bỏ qua test để nhanh hơn
RUN ./mvnw clean package -DskipTests

# Stage 2: Chạy ứng dụng
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
# Copy file JAR từ stage build sang (tên file thường là *-SNAPSHOT.jar)
COPY --from=build /app/target/*.jar app.jar

# Mở port 8080
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]