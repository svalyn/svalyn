#!/bin/bash
cd frontend
npm ci
npm run-script build -- --profile
cd ..
mkdir -p backend/svalyn-application/src/main/resources/static
cp -r frontend/build/* backend/svalyn-application/src/main/resources/static
mvn clean verify -f backend/pom.xml