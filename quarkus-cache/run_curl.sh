#!/bin/bash

curl -i -X GET http://localhost:8080/fruits/
curl -i -X POST -H 'Content-Type: application/json' -d '{"name": "New item", "description": "blabla"}' http://localhost:8080/fruits/
