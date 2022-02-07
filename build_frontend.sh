#!/bin/sh
cd frontend
npm install
npm run build
mkdir -p ../artifacts
rm -rf ../artifacts/frontend
cp -r build ../artifacts/frontend
