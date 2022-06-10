@echo off
call mvn clean package
call docker build -t Effapi/EffAPI .
call docker rm -f EffAPI
call docker run -d -p 9080:9080 -p 9443:9443 --name EffAPI Effapi/EffAPI