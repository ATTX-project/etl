import datetime
import falcon
from wf_api.utils.logs import app_logger
from wf_api.uv.activity_metadata import activity_get_output


class Activity(object):
    """Retrieve the latest activity and associated datasets."""

    def on_get(self, req, resp):
        """Respond on GET request to map endpoint."""
        modifiedSince = req.get_param('modifiedSince')
        output_format = req.get_param('format')

        if output_format is None:
            data = activity_get_output('turtle', modifiedSince)
            result = self.format_response(data)
            if 'data' in result:
                resp.data = result['data']
                resp.content_type = 'text/turtle'
            else:
                resp.last_modifed = lambda x: result['modified'] if result['modified'] is not None else None
            resp.status = result["status"]
        elif output_format == 'json-ld':
            data = activity_get_output('json-ld', modifiedSince)
            result = self.format_response(data)
            if 'data' in result:
                resp.data = result['data']
                resp.content_type = 'application/json+ld'
            else:
                resp.last_modifed = lambda x: result['modified'] if result['modified'] is not None else None
            resp.status = result["status"]
        else:
            resp.content_type = 'text/plain'
            resp.status = falcon.HTTP_405
        app_logger.info('Finished operations on /activity GET Request.')

    def format_response(self, data):
        """Create proper response based on format."""
        if data is 'Empty':
            return {"status": falcon.HTTP_204}
        elif data is not None:
            return {"status": falcon.HTTP_200, "data": data}
        else:
            return {"status": falcon.HTTP_304, "modified": datetime.datetime.now()}
