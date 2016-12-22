from wf_api.api.activity import activity_get, activity_post
from nose.tools import eq_, assert_is_instance
import unittest
from wf_api.app import version, wfm_app
from rdflib import Graph
from flask import Response


class ActivityResponseTest(unittest.TestCase):
    """Test for Activity Response from API."""

    def setUp(self):
        """Set up test fixtures."""
        self.app = wfm_app.app.test_client()
        # propagate the exceptions to the test client
        self.app.testing = True

        self.graph = Graph()

    def tearDown(self):
        """Tear down test fixtures."""
        pass

    def test_activity_post_response(self):
        """Test Activity POST Endpoint responds with a status code."""
        result = self.app.post('/v{0}/activity'.format(version))

        # assert the status code of the response
        eq_(result.status_code, 405)

    def test_activity_get_response(self):
        """Test Activity GET Endpoint responds with a status code."""
        result = self.app.get('/v{0}/activity'.format(version))

        # assert the status code of the response
        if result.data is not '':
            eq_(result.status_code, 200)
        else:
            eq_(result.status_code, 304)

    def test_activity_post_data(self):
        """Test Activity POST Endpoint data response."""
        result = self.app.post('/v{0}/activity'.format(version))

        # assert the status code of the response
        eq_(str(result.data).encode('utf-8'), """Operation Not Allowed.""")

    def test_activity_get_data(self):
        """Test Activity GET Endpoint data response."""
        result = self.app.get('/v{0}/activity'.format(version))

        data = self.graph.parse(data=str(result.data).encode('utf-8'),
                                format='turtle')
        if result.status_code == 200:
            assert_is_instance(data, type(Graph()))

    def test_activity_get(self):
        """Test Activity GET provides a proper Response type."""
        assert_is_instance(activity_get(), type(Response()))

    def test_activity_post(self):
        """Test Activity POST provides a proper Response type."""
        assert_is_instance(activity_post(), type(Response()))


if __name__ == "__main__":
    unittest.main()
