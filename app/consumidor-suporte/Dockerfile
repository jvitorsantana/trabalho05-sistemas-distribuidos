FROM gradle:9.6.0-jdk25 AS build
WORKDIR /app
COPY . .
RUN gradle :consumidor-suporte:installDist --no-daemon

FROM eclipse-temurin:25-jre
ENV JAVA_OPTS="-Dstdout.encoding=UTF-8"
WORKDIR /app
COPY --from=build /app/consumidor-suporte/build/install/consumidor-suporte .
CMD ["/app/bin/consumidor-suporte"]