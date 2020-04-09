###############################################################
# Copyright (c) Stéphane Bégaudeau
#
# This source code is licensed under the MIT license found in
# the LICENSE file in the root directory of this source tree.
###############################################################
FROM node as frontend
WORKDIR /frontend
COPY frontend .
RUN npm ci
RUN npm run-script build

FROM maven:3.6.3-jdk-11 as backend
WORKDIR /backend
COPY backend .
RUN mkdir -p svalyn-application/src/main/resources/static
COPY --from=frontend /frontend/build svalyn-application/src/main/resources/static
RUN ls -la svalyn-application/src/main/resources
RUN ls -la svalyn-application/src/main/resources/static
RUN mvn clean verify
RUN ls -la svalyn-application/target

FROM openjdk:14-jdk-alpine
COPY --from=backend /backend/svalyn-application/target/svalyn-application-0.0.1-SNAPSHOT.jar ./svalyn-application.jar
EXPOSE 8080
RUN adduser -D myuser
USER myuser
CMD [ "sh", "-c", "java -Dserver.port=$PORT -Djava.security.egd=file:/dev/./urandom -jar svalyn-application.jar" ]