#!/bin/sh

dockerize -wait tcp://mysql:3306 -timeout 120s
dockerize -wait http://frontend:8080 -timeout 120s
dockerize -wait http://wfapi:4301/health -timeout 60s

gradle -b build.gradle --offline test
