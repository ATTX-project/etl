from flask import Response
from wf_api.uv.workflow_metadata import workflow_get_output
import datetime
from wf_api.utils.logs import app_logger


def workflow_get(modifiedSince=None):
    """List the latest workflow and associated steps."""
    data = workflow_get_output('turtle')
    workflow = ''
    if data != "No Workflow to be loaded.":
        workflow = Response(
            response=data,
            status=200,
            mimetype='text/turtle')
        app_logger.info('Workflow Reponse is 200 OK.')
    else:
        now = datetime.datetime.now()
        workflow = Response(status=304)
        workflow.headers['Last-Modified'] = now
        app_logger.info('Workflow Reponse is 304 Not Modified.')
    app_logger.info('Reponse from the Workflow API was issued.')
    return workflow


def workflow_post():
    """Operation cannot be perfomed."""
    app_logger.info('Reponse from the Workflow API is: POST not allowed.')
    response = Response(
        response='Operation Not Allowed.',
        status=405,
        mimetype='text/plain')
    return response
