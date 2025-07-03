# Stage 1: Build
FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /app
# Copiar archivos Maven
COPY pom.xml mvnw ./
COPY .mvn .mvn
# Descargar dependencias sin construir
RUN mvn dependency:go-offline -B
# Copiar el código fuente
COPY src src
# Compilar y empaquetar
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copiar JAR desde builder
COPY --from=builder /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=dev \
    JAVA_OPTS="-Xms50m -Xmx50m"

# Comando de arranque
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]