FROM eclipse-temurin:11-jre

WORKDIR /app

COPY target/universal/stage/ .

EXPOSE 9000

CMD ["bin/bloodbank"]