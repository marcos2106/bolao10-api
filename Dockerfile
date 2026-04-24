# ---- Builder stage ----
FROM eclipse-temurin:8-jdk AS builder

WORKDIR /app

# Copia projeto
COPY . .

# Usa Maven Wrapper para buildar o JAR
RUN chmod +x mvnw && ./mvnw -B -DskipTests clean package

# ---- Runtime stage ----
FROM eclipse-temurin:8-jre AS runtime

WORKDIR /app

# Copia apenas o JAR gerado
COPY --from=builder /app/target/*jar /app/app.jar

EXPOSE 8080

CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
