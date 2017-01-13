from flask import Response
from wf_api.uv.workflow_metadata import workflow_get_output
import datetime
from wf_api.utils.logs import app_logger


def workflow_get(modifiedSince=None, format=None):
    """List the latest workflow and associated steps."""
    workflow = ''
    if format is None:
        data = workflow_get_output('turtle', modifiedSince)
        workflow = format_response(data, 'turtle')
    elif format == 'json-ld':
        data = workflow_get_output('json-ld', modifiedSince)
        workflow = format_response(data, format)
    else:
        workflow = Response(
            response='Operation Not Allowed.',
            status=405,
            mimetype='text/plain')
    app_logger.info('Reponse from the workflow API was issued.')
    return workflow


def workflow_post():
    """Operation cannot be perfomed."""
    app_logger.info('Reponse from the Workflow API is: POST not allowed.')
    response = Response(
        response='Operation Not Allowed.',
        status=405,
        mimetype='text/plain')
    return response


def not_modified():
    """Response for 304: Not Modified."""
    now = datetime.datetime.now()
    response = Response(status=304)
    response.headers['Last-Modified'] = now
    app_logger.info('workflow Reponse is 304 Not Modified.')
    return response


def format_response(data, resp_format):
    """Create proper response based on format."""
    formats = {
        'turtle': 'text/turtle',
        'json-ld': 'application/json+ld'
    }
    if data is not None:
        workflow = Response(
            response=data,
            status=200,
            mimetype=formats[resp_format])
        app_logger.info('workflow Reponse is 200 OK.')
        return workflow
    else:
        return not_modified()
