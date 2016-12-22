## WF API

Current directory contains:
* WF-API implementation in `server` folder
* Docker container and implementation for the WF API

VERSION: 0.1

### WF API Docker container

Using the WF API Docker container:
* build the container `docker build -t attxproject/wfapi .` in the current folder;
* running the container `docker run -d -p 4301:4301 attxproject/wfapi` runs the container in detached mode on the `4301` port (production version should have this port hidden);
* connect to the address of your server e.g. `http://localhost:4301/v$versionNb/ui` for local development and a swagger UI will be available;
* using the endpoints `http://localhost:4301/v0.1/activity` or `http://localhost:4301/v$versionNb/workflow`.

The version number is specified in `server/app.py` under `version` variable.
In order to run this in a docker container with the unified views and WF stack the databased.conf file variable `host` needs to be set to `mysql` or specific address of the databased container.
This container will be part of the docker-compose run together with the unifiedviews tool.


# WF API server

## Overview
The WF exposes information fromthe ETL component (intial use case is the [UnifiedViews](https://github.com/ATTX-project/etl/tree/feature-uv-docker/unifiedViews)
and the specific [ATTX metadata plugin](https://github.com/ATTX-project/etl/tree/feature-uv-docker/unifiedViews/dpu-t-attx-metadata) ) in order to exposes Workflow and Activity related information
characteristic to the ETL process. Such information can be consumed in order to be integrated in a graph database.

The WF API requires python3 installed.
To run the server, please execute the following (preferably in a virtual environment):

```
pip install -r requirements
python app.py
```
or `gunicorn -w 2 -b 127.0.0.1:4301 app:wfm_app` in the `server/` folder

and open the browser to here:

```
http://localhost:4301/v$versionNb/ui
```

The Swagger definition lives here:

```
http://localhost:4301/v$versionNb/swagger.json
```

Where `$versionNb` is the version number specified under `app.py` in `version` variable.

for the database configuration see `database.conf` file.


## Running Tests

In order work/generate tests:
* use the command: `nosetests tests` in the `server` folder.
* coverage: `nosetests --with-coverage --cover-erase --cover-package . ` run in the `server` folder.
* generate cover report `nosetests --with-coverage --cover-erase --cover-package . --cover-html` - this will generate a `cover` folder that contains html based information
about the tests coverage.
