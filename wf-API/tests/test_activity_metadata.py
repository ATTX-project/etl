from wf_api.uv.activity_metadata import activity_get_output, ActivityGraph
from rdflib import Graph
from nose.tools import eq_, assert_is_instance
import unittest
from nose.tools import eq_


class ActivityGraphTest(unittest.TestCase):
    """Test for Activity Response from API."""

    def setUp(self):
        """Set up test fixtures."""
        self.graph = Graph()
        self.format_jsonld = 'json-ld'
        self.format_turtle = 'turtle'
        self.data = ActivityGraph()
        self.activity_graph = self.data.activity('2017-01-17T14:14:14Z')

    def tearDown(self):
        """Tear down test fixtures."""
        pass

    def test_activity_get_output(self):
        """Test Activity processing output is Graph."""
        if activity_get_output('turtle', None) is None:
            eq_(activity_get_output('turtle', None), None)
        else:
            data_turtle = self.graph.parse(data=str(activity_get_output(self.format_turtle, None))
                                           .encode('utf-8'),
                                           format=self.format_turtle)
            data_json = self.graph.parse(data=str(activity_get_output(self.format_jsonld, None))
                                         .encode('utf-8'),
                                         format=self.format_jsonld)
            assert_is_instance(data_turtle, type(Graph()))
            assert_is_instance(data_json, type(Graph()))

    def test_activity_no_output(self):
        """Test Activity processing output is empty graph."""
        if len(self.activity_graph) == 0:
            assert True


if __name__ == "__main__":
    unittest.main()
