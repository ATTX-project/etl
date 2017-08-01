#!/bin/sh

# Wait for MySQL, the big number is because CI is slow.
dockerize -wait tcp://mysql:3306 -timeout 240s
dockerize -wait http://wfapi:4301/health -timeout 60s

echo  "Archiva repository URL: $REPO"

gradle -PartifactRepoURL=$REPO integTest
