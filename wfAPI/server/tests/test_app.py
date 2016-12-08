from app import wfm_app
import urllib
# from flask import Flask
from flask_testing import LiveServerTestCase


class TestApp(LiveServerTestCase):
    """Test app is ok."""

    def create_app(self):
        """Set up."""
        # app = Flask(__name__)
        wfm_app.config['TESTING'] = True
        # Default port is 5000
        wfm_app.config['LIVESERVER_PORT'] = 4301
        # Default timeout is 5 seconds
        wfm_app.config['LIVESERVER_TIMEOUT'] = 10
        return wfm_app

    def test_server_is_up_and_running(self):
        """Test the server is up and running."""
        response = urllib.urlopen(self.get_server_url())
        self.assertEqual(response.code, 200)
