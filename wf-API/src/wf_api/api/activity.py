import datetime
from flask import Response
from wf_api.utils.logs import app_logger
from wf_api.uv.activity_metadata import activity_get_output


def activity_get(modifiedSince=None, format=None):
    """Retrieve the latest activity and associated datasets."""
    activity = ''
    if format is None:
        data = activity_get_output('turtle', modifiedSince)
        activity = format_response(data, 'turtle')
    elif format == 'json-ld':
        data = activity_get_output('json-ld', modifiedSince)
        activity = format_response(data, format)
    else:
        activity = Response(response='Operation Not Allowed.', status=405,
                            mimetype='text/plain')
    app_logger.info('Reponse from the Activity API was issued.')
    return activity


def activity_post():
    """Operation cannot be perfomed."""
    app_logger.warning('Response from the Activity API is: POST not allowed.')
    response = Response(response='Operation Not Allowed.', status=405,
                        mimetype='text/plain')
    return response


def not_modified():
    """Response for 304: Not Modified."""
    now = datetime.datetime.now()
    response = Response(status=304)
    response.headers['Last-Modified'] = now
    app_logger.info('Activity Reponse is 304 Not Modified.')
    return response


def no_content():
    """Response for 204: No Content."""
    response = Response(status=204)
    app_logger.info('Activity Reponse is 204 No Content.')
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
        app_logger.info('Activity Reponse is 200 OK.')
        return activity
    else:
        return not_modified()
