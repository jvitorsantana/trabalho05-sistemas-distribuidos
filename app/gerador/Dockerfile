FROM gradle:9.6.0-jdk25 AS build
WORKDIR /app
COPY . .
RUN gradle :gerador:installDist --no-daemon

FROM eclipse-temurin:25-jre
ENV JAVA_OPTS="-Dstdout.encoding=UTF-8"
WORKDIR /app
COPY --from=build /app/gerador/build/install/gerador .
CMD ["/app/bin/gerador"]