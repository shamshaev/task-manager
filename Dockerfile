FROM gradle:8.8.0-jdk21

WORKDIR /app

COPY . /app

RUN gradle installDist

CMD ./build/install/app/bin/app

#FROM ubuntu:latest
#LABEL authors="andrey"

#ENTRYPOINT ["top", "-b"]