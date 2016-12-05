## ETL API

Current directory contains:
* swagger specification for the ETL API
* Docker container and implementation for the ETL API

### ETL API Docker container

Using the ETL API Docker container:
* build the container `docker build -t attxproject/etlapi .` in the etlAPI folder;
* running the container `docker run -d -p 4301:4301 attxproject/etlapi` runs the container in detached mode on the `4301` port;
* connect to the address of your server e.g. `http://localhost:4301/ui` for local development and a swagger UI will be available;
* using the endpoints `http://localhost:4301/activity` or `http://localhost:4301/workflow`.

This container will be part of the docker-compose run together with the unifiedviews tool.
