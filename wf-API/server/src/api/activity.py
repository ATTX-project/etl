from flask import Response
from uv.activity_metadata import activity_get_output
import datetime
from logs import app_logger


def activity_get(modifiedSince=None):
    """Retrieve the latest activity and associated datasets."""
    data = activity_get_output('turtle')
    activity = ''
    if data != "No activity to be loaded.":
        activity = Response(
            response=data,
            status=200,
            mimetype='text/turtle')
        app_logger.info('Activity Reponse is 200 OK.')
    else:
        now = datetime.datetime.now()
        headers = {'Last-Modified': now}
        activity = Response(
            headers=headers,
            status=304)
        app_logger.info('Activity Reponse is 304 Not Modified.')
    app_logger.info('Reponse from the Activity API was issued.')
    return activity


def activity_post():
    """Operation cannot be perfomed."""
    app_logger.info('Reponse from the Activity API is: POST not allowed.')
    response = Response(
        response='Operation Not Allowed.',
        status=405,
        mimetype='text/plain')
    return response
