## ATTX WF Component

The ATTX Workflow Component provides a configurable framework with the main purpose of ingesting, processing and distributing data and at the same time keeping track of the provenance, workflow/pipelines and activities/scheduled jobs used to map and enhance/enrich the information.

The Workflow Component consists of two main artifacts:
* ETL Engine/Artifact - we make use of [UnifiedViews](https://unifiedviews.eu/) as an ETL tool along side custom plugins;
* Workflow API - which extracts provenance information from UnifiedViews Pipelines and Pipeline Executions.

## Repository Structure

The repository consists of:
* UnifiedViews custom plugins:
    * uv-dpu-l-attx-apicaller
    * uv-dpu-l-attx-esindexer
    * uv-dpu-l-attx-linker
    * uv-dpu-l-attx-uploader
    * uv-dpu-t-attx-metadata
    * uv-dpu-t-attx-uc1-infras2internal
* Workflow API
* Workflow Component - Integration Tests
