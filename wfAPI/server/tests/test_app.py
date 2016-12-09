from app import wfm_app
import unittest
from nose.tools import eq_


class TestApp(unittest.TestCase):
    """Test app is ok."""

    def setUp(self):
        """Set up test fixtures."""
        self.app = wfm_app.app.test_client()
        # propagate the exceptions to the test client
        self.app.testing = True

    def teadDown(self):
        """Tear down test fixtures."""
        pass

    def test_main(self):
        """Test the server is up and running."""
        response = self.app.get('/')
        eq_(response.status_code, 404)


if __name__ == "__main__":
    unittest.main()
