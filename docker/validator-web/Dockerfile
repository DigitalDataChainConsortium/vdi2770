#FROM openjdk:18
FROM debian:bullseye-slim

LABEL org.opencontainers.image.authors="Johannes Schmidt" \
      org.opencontainers.image.vendor="DigitalDataChainConsortium" \
      org.opencontainers.image.licenses="MIT"

RUN apt update && apt upgrade --yes && apt install locales locales-all openjdk-17-jre-headless --yes

ENV LANG C.UTF-8 
ENV LANGUAGE C.UTF-8
ENV LC_ALL C.UTF-8

RUN dpkg-reconfigure --frontend=noninteractive locales
	
RUN mkdir -p /app
WORKDIR /app

ARG JAR_FILE=vdi2770-validator-web.jar
COPY app.properties /app/app.properties

ENV TOKEN=vdi2770
ENV DOMAINS=http://localhost:3000
ENV FILELIMIT=5MB

RUN mkdir /app/log  
VOLUME /app/log

RUN mkdir /app/static  
VOLUME /app/static

COPY ${JAR_FILE} /app/vdi2770.jar

ENTRYPOINT ["java","-jar","/app/vdi2770.jar","--vdi2770.http.auth.tokenValue=${TOKEN}","--vdi2770.http.cors.domains=${DOMAINS}","--spring.servlet.multipart.max-file-size=${FILELIMIT}","--spring.servlet.multipart.max-request-size=${FILELIMIT}"]