#!/bin/sh
mvn clean package && docker build -t Effapi/EffAPI .
docker rm -f EffAPI || true && docker run -d -p 9080:9080 -p 9443:9443 --name EffAPI Effapi/EffAPI