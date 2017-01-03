from wf_api.uv.activity_metadata import activity_get_output
from rdflib import Graph
from nose.tools import eq_, assert_is_instance
import unittest


class ActivityGraphTest(unittest.TestCase):
    """Test for Activity Response from API."""

    def setUp(self):
        """Set up test fixtures."""
        self.graph = Graph()

    def tearDown(self):
        """Tear down test fixtures."""
        pass

    def test_activity_get_output(self):
        """Test Activity processing output is Graph."""
        if activity_get_output('turtle', None) is None:
            eq_(activity_get_output('turtle', None), None)
        else:
            data = self.graph.parse(data=str(activity_get_output('turtle',
                                                                 None))
                                    .encode('utf-8'),
                                    format='turtle')
            assert_is_instance(data, type(Graph()))


if __name__ == "__main__":
    unittest.main()
