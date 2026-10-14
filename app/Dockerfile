FROM gradle:9.6.0-jdk25 AS build
WORKDIR /app
COPY . .
RUN gradle installDist --no-daemon

FROM eclipse-temurin:25-jre
ARG MODULO
ENV MODULO=${MODULO}

ENV JAVA_OPTS="-Dstdout.encoding=UTF-8"
WORKDIR /app
COPY --from=build /app/${MODULO}/build/install/${MODULO} .
CMD ["sh", "-c", "exec bin/${MODULO}"]