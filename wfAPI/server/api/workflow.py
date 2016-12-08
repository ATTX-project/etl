from flask import Response
from uv.workflow_metadata import construct_output
import datetime
import logging
import logging.config

logging.config.fileConfig('logging.conf')
logger = logging.getLogger('appLogger')


def workflow_get(modifiedSince=None):
    """List the latest workflow and associated steps."""
    data = construct_output('turtle')
    workflow = ''
    if data != "No new Workflow to be loaded.":
        workflow = Response(
            response=data,
            status=200,
            mimetype='text/turtle')
        logger.info('Workflow Reponse is 200 OK.')
    else:
        now = datetime.datetime.now()
        workflow = Response(status=304)
        workflow.headers['Last-Modified'] = now
        logger.info('Workflow Reponse is 304 Not Modified.')
    logger.info('Reponse from the Workflow API was issued.')
    return workflow


def workflow_post():
    """Operation cannot be perfomed."""
    logger.info('Reponse from the Workflow API is: POST not allowed.')
    return """Operation Not Allowed.""", 405
