FROM gradle:jdk23 AS builder
WORKDIR /app

COPY . .

RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:23-jre-alpine
LABEL authors="MAGiC-V Team"
LABEL version="1.0.0"

WORKDIR /app

COPY --from=builder /app/build/libs/eventimusicali-1.0.0-all.jar app.jar
COPY --from=builder /app/db.sqlite db.sqlite

ENTRYPOINT ["java", "-jar", "app.jar"]
