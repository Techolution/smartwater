FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/* /target/

CMD ["java","-jar","/target/eurekaservice-0.0.1-SNAPSHOT.jar"]