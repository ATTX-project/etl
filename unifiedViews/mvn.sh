#!/bin/bash
mvn -Dmaven.repo.remote=http://archiva:8080/repository/internal clean package
