import falcon
from wf_api.api.activity import Activity
from wf_api.api.workflow import Workflow
from wf_api.utils.logs import main_logger

api_version = "0.1"  # TO DO: Figure out a better way to do versioning


def create():
    """Create the API endpoint."""
    do_activity = Activity()
    do_workflow = Workflow()

    gm_app = falcon.API()
    gm_app.add_route('/%s/activity' % (api_version), do_activity)
    gm_app.add_route('/%s/workflow' % (api_version), do_workflow)
    main_logger.info('App is running.')
    return gm_app


if __name__ == '__main__':
    create()
