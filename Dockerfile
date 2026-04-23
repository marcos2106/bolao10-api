# ---- Builder stage ----
FROM openjdk:8-jdk-alpine AS builder

# Install Maven
RUN apk add --no-cache maven

WORKDIR /app

# Copy project files
COPY . .

# Set JAVA_HOME explicitly and run the Maven build
ENV JAVA_HOME=/usr/lib/jvm/java-1.8-openjdk
RUN chmod +x mvnw && \
    ./mvnw -DoutputFile=target/mvn-dependency-list.log -B -DskipTests clean dependency:list install -Pproduction

# ---- Runtime stage ----
FROM openjdk:8-jre-alpine AS runtime

WORKDIR /app

COPY --from=builder /app/target/*jar /app/app.jar

EXPOSE 8080

CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
