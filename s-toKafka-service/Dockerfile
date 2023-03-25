# TWO-STAGE BUILD DOCKER FILE

ARG BUILD_IMAGE=maven:3.8.3-openjdk-11
ARG RUNTIME_IMAGE=openjdk:11

#############################################################################################
###                Stage where Docker is pulling all maven dependencies                   ###
#############################################################################################
FROM ${BUILD_IMAGE} as dependencies

###OPY ./custom-security-starter ./custom-security-starter

COPY ../pom.xml ../pom.xml
COPY pom.xml ./

RUN mvn -B dependency:go-offline
#############################################################################################

#############################################################################################
###              Stage where Docker is building spring boot app using maven               ###
#############################################################################################
FROM dependencies as build

COPY ./src ./src

RUN mvn -B clean package
#############################################################################################

#############################################################################################
### Stage where Docker is running a java process to run a service built in previous stage ###
#############################################################################################
FROM ${RUNTIME_IMAGE}
ARG JAR_FILE=target/s-toKafka-service-0.0.1-SNAPSHOT.jar
COPY --from=build ${JAR_FILE} app.jar
CMD ["java","-jar","app.jar"]
#############################################################################################

