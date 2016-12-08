from flask import Response
from uv.activity_metadata import construct_output
import datetime
import logging
import logging.config

logging.config.fileConfig('logging.conf')
logger = logging.getLogger('appLogger')


def activity_get(modifiedSince=None):
    """Retrieve the latest activity and associated datasets."""
    data = construct_output('turtle')
    activity = ''
    if data != "No new activity to be loaded.":
        activity = Response(
            response=data,
            status=200,
            mimetype='text/turtle')
    else:
        now = datetime.datetime.now()
        headers = {'Last-Modified': now}
        activity = Response(
            headers=headers,
            status=304)
    return activity


def activity_post():
    """Operation cannot be perfomed."""
    return """Operation Not Allowed.""", 405
