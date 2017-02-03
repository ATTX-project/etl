## WF API

Current directory contains:
* WF-API implementation in `src/wf-API` folder
* Docker container and implementation for the WF API

VERSION: 0.1

### WF API Docker container

Using the WF API Docker container:
* build the container `docker build -t attxproject/wfapi .` in the current folder;
* running the container `docker run -d -p 4301:4301 attxproject/wfapi` runs the container in detached mode on the `4301` port (production version should have this port hidden);
* connect to the address of your server e.g. `http://localhost:4301/v$versionNb/ui` for local development and a swagger UI will be available;
* using the endpoints `http://localhost:4301/v$versionNb/activity` or `http://localhost:4301/v$versionNb/workflow`.

The version number is specified in `src/wf-API/wfapi.py` under `version` variable.
In order to run this in a docker container with the unified views and WF stack the databased.conf file variable `host` needs to be set to `mysql` or specific address of the database container (`localhost` for running locally the app).
This container will be part of the docker-compose run together with the unifiedviews tool.


# WF API server

## Overview
The WF exposes information fromthe ETL component (intial use case is the [UnifiedViews](https://github.com/ATTX-project/etl/tree/feature-uv-docker/unifiedViews)
and the specific [ATTX metadata plugin](https://github.com/ATTX-project/etl/tree/feature-uv-docker/unifiedViews/dpu-t-attx-metadata) ) in order to exposes Workflow and Activity related information
characteristic to the ETL process. Such information can be consumed in order to be integrated in a graph database.

The WF API requires python 2.7.6 installed.
To run the server, please execute the following (preferably in a virtual environment):

### Build with Gradle

Install [gradle](https://gradle.org/gradle-download/?_ga=1.226518941.1083404848.1481538559) and run `gradle wrapper`. The tasks available are listed below:

* clean: `./gradlew clean`
* build: `./gradlew build`
* other tasks: `./gradlew :runTests` or `./gradlew :wf-API:pytest`
* exclude tasks: `./gradlew build -x :wf-API:pytest`
* debugging: `./gradlew :wf-API:testsReport --stacktrace`
* do all: `./gradlew clean build -x :wf-API:pytest -x :wf-API:testsReport`
* see tasks: `./gradlew tasks` and depenencies `./gradlew depenencies`
* see test coverage `./gradlew :wf-API:pytest coverage` it will generate a html report in `build/coverage/htmlcov`

### Build without Gradle

```
pip install -r requirements
python app.py
```
or `python src/wf-API/wfapi.py` in the `wf-API` folder

and open the browser to here:

```
http://localhost:4301/v$versionNb/ui
```

The Swagger definition lives here:

```
http://localhost:4301/v$versionNb/swagger.json
```

Where `$versionNb` is the version number specified under `app.py` in `version` variable (used in the URL), but also in the `build.gradle` (same variable but used for the build).

for the database configuration see `database.conf` file.


## Running Tests

In order work/generate tests:
* use the command: `py.test tests` in the `wf-API` folder
* coverage: `py.test --cov-report html --cov=wf-API tests/` in the `wf-API` folder
* generate cover report `py.test tests --html=build/test-report/index.html --self-contained-html` - this will generate a `build/test-report/index.html` folder that contains html based information about the tests coverage.
