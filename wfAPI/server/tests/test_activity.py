from api.activity import activity_get, activity_post
from nose.tools import eq_, ok_
import unittest
from app import version, wfm_app


class ActivityResponseTest(unittest.TestCase):
    """Test for Activity Response from API."""

    def setUp(self):
        """Set up test fixtures."""
        self.app = wfm_app.app.test_client()
        # propagate the exceptions to the test client
        self.app.testing = True

    def teadDown(self):
        """Tear down test fixtures."""
        pass

    def test_activity_post_response(self):
        """Test POST Endpoint responds properly."""
        result = self.app.post('/v{0}/activity'.format(version))

        # assert the status code of the response
        eq_(result.status_code, 405)

    def test_activity_get_response(self):
        """Test GET Endpoint responds properly."""
        result = self.app.get('/v{0}/activity'.format(version))

        # assert the status code of the response
        eq_(result.status_code, 200)

    def test_activity_post_data(self):
        """Test GET Endpoint responds properly."""
        result = self.app.post('/v{0}/activity'.format(version))

        # assert the status code of the response
        eq_(str(result.data, 'utf-8'), """Operation Not Allowed.""")

    def test_activity_get_data(self):
        """Test GET Endpoint responds properly."""
        result = self.app.get('/v{0}/activity'.format(version))

        # assert the status code of the response
        eq_(result.data, 200)


if __name__ == "__main__":
    unittest.main()
