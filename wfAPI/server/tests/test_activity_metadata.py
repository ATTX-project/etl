from uv.activity_metadata import ActivityGraph, activity_get_output
from rdflib import Graph, Namespace
from rdflib.compare import similar
from nose.tools import eq_, ok_
import unittest


class ActivityGraphTest(unittest.TestCase):
    """Test for Activity Response from API."""

    def setUp(self):
        """Set up test fixtures."""
        self.graph = Graph()
        self.test_graph = Graph()
        self.activityId = '1'
        self.namespace = Namespace('http://helsinki.fi/library/onto#')
        self.graph.bind('kaisa', 'http://helsinki.fi/library/onto#')
        self.graph.bind('dc', 'http://purl.org/dc/elements/1.1/')
        self.graph.bind('dcterms', 'http://purl.org/dc/terms/')
        self.graph.bind('prov', 'http://www.w3.org/ns/prov#')
        self.graph.bind('sd',
                        'http://www.w3.org/ns/sparql-service-description#')

        # Tests are run from top directory
        self.config = open('tests/examples/config.xml', 'r')
        self.test_graph.parse('tests/examples/activity.ttl', format='turtle')

    def teadDown(self):
        """Tear down test fixtures."""
        self.graph.destroy()

    def test_activity_post(self):
        """Test POST Endpoint responds properly."""

    def test_activity_get_output(self):
        """Test GET Endpoint responds properly."""
        # result = Graph()
        # test = result.parse(activity_get_output('turtle'), format='turtle')
        # Considering blank nodes we need t check if the graphs are similar
        # eq_(similar(test, self.test_graph), True,
            # "Test to if the resulting graph corresponds to activity graph.")


if __name__ == "__main__":
    unittest.main()
