from uv.workflow_metadata import WorkflowGraph, workflow_get_output
from nose.tools import eq_, ok_
import unittest


class WorkflowGraphTest(unittest.TestCase):
    """Test for Activity Response from API."""

    def setUp(self):
        """Set up test fixtures."""

    def teadDown(self):
        """Tear down test fixtures."""
        self.graph.destroy()

    def test_activity_post(self):
        """Test POST Endpoint responds properly."""

    def test_construct_output(self):
        """Test GET Endpoint responds properly."""


if __name__ == "__main__":
    unittest.main()
