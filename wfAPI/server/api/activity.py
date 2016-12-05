from api.serialization import convert
import pymysql as mysql
from flask import Response

def activity_get(modifiedSince = None):
    """Retrieve the latest activity and associated datasets.

    This operation gathers all the information necessary to describe the activities, datasets and how the datasets are acquired (input dataset) or produced (output dataset).
    """
    data = convert('tests/activity.ttl', 'json-ld')
    the_activity = Response (
        response = data,
        status = 200,
        mimetype = 'application/json-ld'
    )
    return the_activity

def activity_post():
    """ This operation cannot be Perfomed."""
    return """Operation Not Allowed.""", 405
