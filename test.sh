#!/bin/bash
curl -X POST -v --data-binary "@pom.xml" -H "Accept: text/plain" -H "Content-type: application/octet-stream" http://localhost:8080/reactive/gateway/upload