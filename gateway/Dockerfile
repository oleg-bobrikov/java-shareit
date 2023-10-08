FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar app.jar
#For remote debugging only, 8081 is a port for listening
#ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
ENTRYPOINT ["java","-jar","/app.jar"]