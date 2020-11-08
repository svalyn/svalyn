###############################################################
# Copyright (c) Stéphane Bégaudeau
#
# This source code is licensed under the MIT license found in
# the LICENSE file in the root directory of this source tree.
###############################################################
FROM openjdk:15-jdk-alpine
COPY backend/svalyn-application/target/svalyn-application-0.0.1-SNAPSHOT.jar ./svalyn-application.jar
EXPOSE 8080
RUN adduser -D myuser
USER myuser
CMD [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar svalyn-application.jar" ]