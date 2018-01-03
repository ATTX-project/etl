## ATTX Workflow Component

The ATTX Workflow Component provides a configurable framework with the main purpose of ingesting, processing and distributing data and at the same time keeping track of the provenance, workflow/pipelines and activities/scheduled jobs used to map and enhance/enrich the information.

The Workflow Component consists of two main artifacts:
* ETL Engine/Artifact - we make use of [UnifiedViews](https://unifiedviews.eu/) as an ETL tool along side custom plugins;
* UV Provenance Service - which extracts provenance information from UnifiedViews Pipelines and Pipeline Executions.

### Clone Repository
```
    git clone --recursive https://github.com/ATTX-project/workflow-component

    git submodule update --init --recursive
    
    git submodule update --init --recursive --remote
```
