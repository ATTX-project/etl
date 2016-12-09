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