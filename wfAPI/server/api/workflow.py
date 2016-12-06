from flask import Response
from uv.workflow_metadata import construct_output
import datetime


def workflow_get(modifiedSince=None):
    """List the latest workflow and associated steps."""
    data = construct_output('turtle')
    workflow = ''
    if data != "No new Workflow to be loaded.":
        workflow = Response(
            response=data,
            status=200,
            mimetype='text/turtle')
    else:
        now = datetime.datetime.now()
        headers = {'Last-Modified': now}
        workflow = Response(
            headers=headers,
            status=304)
    return workflow


def workflow_post():
    """Operation cannot be perfomed."""
    return """Operation Not Allowed.""", 405
