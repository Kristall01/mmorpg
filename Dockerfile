FROM openjdk:11

WORKDIR /server
ARG backend=./artifacts/backend.jar
ARG frontend=./artifacts/frontend
COPY ${backend} /server/server.jar
COPY ${frontend} /server/frontend
ENV port=8080
ENV serve=frontend
CMD ["java","-jar","server.jar"]
