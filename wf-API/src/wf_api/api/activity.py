from flask import Response
from wf_api.uv.activity_metadata import activity_get_output
import datetime
from wf_api.utils.logs import app_logger


# TO DO: figure out using dict how to replace the multiple if
def activity_get(modifiedSince=None, format=None):
    """Retrieve the latest activity and associated datasets."""
    activity = ''
    if format is None:
        data = activity_get_output('turtle')
        if data != "No Activity to be loaded.":
            activity = Response(
                response=data,
                status=200,
                mimetype='text/turtle')
            app_logger.info('Activity Reponse is 200 OK.')
        else:
            now = datetime.datetime.now()
            activity = Response(status=304)
            activity.headers['Last-Modified'] = now
            app_logger.info('Activity Reponse is 304 Not Modified.')
    elif format == 'json-ld':
        data = activity_get_output('json-ld')
        if data != "No Activity to be loaded.":
            activity = Response(
                response=data,
                status=200,
                mimetype='application/json+ld')
            app_logger.info('Activity Reponse is 200 OK.')
        else:
            now = datetime.datetime.now()
            activity = Response(status=304)
            activity.headers['Last-Modified'] = now
            app_logger.info('Activity Reponse is 304 Not Modified.')
    else:
        activity = Response(
            response='Operation Not Allowed.',
            status=405,
            mimetype='text/plain')
    app_logger.info('Reponse from the Activity API was issued.')
    return activity


def activity_post():
    """Operation cannot be perfomed."""
    app_logger.info('Response from the Activity API is: POST not allowed.')
    response = Response(
        response='Operation Not Allowed.',
        status=405,
        mimetype='text/plain')
    return response
