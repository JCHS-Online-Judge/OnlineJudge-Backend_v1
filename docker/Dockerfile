FROM ubuntu:22.04

RUN apt-get update \
    && apt-get install -y software-properties-common \
    && add-apt-repository ppa:deadsnakes/ppa \
    && apt-get update \
    && apt-get install -y openjdk-17-jdk python3.10 gcc g++ \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

ENV MONGODB_HOST localhost
ENV MONGODB_PORT 27017
ENV MONGODB_USERNAME (USERNAME)
ENV MONGODB_PASSWORD (PASSWORD)
ENV MONGODB_AUTH_DATABASE admin
ENV MONGODB_DATABASE oj

WORKDIR /app

COPY ./judge.jar .

ENTRYPOINT ["sh", "-c", "java -jar judge.jar"]
