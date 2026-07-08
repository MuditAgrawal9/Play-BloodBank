# ---------- Stage 1 : Build ----------
FROM eclipse-temurin:11-jdk AS builder

ARG SBT_VERSION=1.12.12

RUN apt-get update && \
    apt-get install -y curl gnupg2 && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" > /etc/apt/sources.list.d/sbt.list && \
    curl -fsSL https://keyserver.ubuntu.com/pks/lookup?op=get\&search=0x99E82A75642AC823 | gpg --dearmor -o /etc/apt/trusted.gpg.d/sbt.gpg && \
    apt-get update && \
    apt-get install -y sbt=${SBT_VERSION}* && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Cache dependencies
COPY build.sbt .
COPY project ./project

RUN sbt update

# Copy source
COPY app ./app
COPY conf ./conf
COPY public ./public

# Build Play application
RUN sbt clean stage

# ---------- Stage 2 : Runtime ----------
FROM eclipse-temurin:11-jre

WORKDIR /app

COPY --from=builder /app/target/universal/stage/ .

EXPOSE 9000

CMD ["bin/bloodbank"]