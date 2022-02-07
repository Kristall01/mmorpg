#!/bin/sh
cd backend
/bin/sh ./gradlew build
mkdir -p ../artifacts
rm -rf ../artifacts/backend.jar
cp build/libs/server.jar ../artifacts/backend.jar
