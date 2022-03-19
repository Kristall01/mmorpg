FROM openjdk:11

WORKDIR /server
COPY ./artifacts/bakcend.jar /server/server.jar
COPY ./artifacts/frontend /server/frontend
ENV port=8080
ENV serve=frontend
RUN ["java","-jar","server.jar"]
