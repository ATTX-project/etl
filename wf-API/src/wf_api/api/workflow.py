import datetime
from flask import Response
from wf_api.utils.logs import app_logger
from wf_api.uv.workflow_metadata import workflow_get_output


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
        workflow = Response(response='Operation Not Allowed.', status=405,
                            mimetype='text/plain')
    app_logger.info('Reponse from the Workflow API was issued.')
    return workflow


def workflow_post():
    """Operation cannot be perfomed."""
    app_logger.warning('Reponse from the Workflow API is: POST not allowed.')
    response = Response(response='Operation Not Allowed.', status=405,
                        mimetype='text/plain')
    return response


def not_modified():
    """Response for 304: Not Modified."""
    now = datetime.datetime.now()
    response = Response(status=304)
    response.headers['Last-Modified'] = now
    app_logger.info('Workflow Reponse is 304 Not Modified.')
    return response


def no_content():
    """Response for 204: No Content."""
    now = datetime.datetime.now()
    response = Response(status=204)
    response.headers['Last-Modified'] = now
    app_logger.info('Workflow Reponse is 204 No Content.')
    return response


def format_response(data, resp_format):
    """Create proper response based on format."""
    formats = {
        'turtle': 'text/turtle',
        'json-ld': 'application/json+ld'
    }
    if data is 'Empty':
        return no_content()
    elif data is not None:
        activity = Response(response=data, status=200, mimetype=formats[resp_format])
        app_logger.info('Workflow Reponse is 200 OK.')
        return activity
    else:
        return not_modified()
