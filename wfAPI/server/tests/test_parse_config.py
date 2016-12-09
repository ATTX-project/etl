from uv.parse_config import parse_metadata_config
from rdflib import Graph, Namespace
from rdflib.compare import similar, graph_diff, to_isomorphic, isomorphic
from nose.tools import eq_, ok_
import unittest


class ParseConfigTest(unittest.TestCase):
    """Test for Prasing UnifiedViews plugin specific configuration."""

    def setUp(self):
        """Set up test fixtures."""
        self.graph = Graph()
        self.test_graph = Graph()
        self.activityId = '1'
        self.graph.namespace = Namespace('http://helsinki.fi/library/onto#')
        self.graph.bind('kaisa', 'http://helsinki.fi/library/onto#')
        self.graph.bind('dc', 'http://purl.org/dc/elements/1.1/')
        self.graph.bind('dcterms', 'http://purl.org/dc/terms/')
        self.graph.bind('prov', 'http://www.w3.org/ns/prov#')
        self.graph.bind('pwo', 'http://purl.org/spar/pwo/')
        self.graph.bind('sd',
                        'http://www.w3.org/ns/sparql-service-description#')

        # Tests are run from top directory
        self.config = open('tests/examples/config.xml', 'r')
        self.test_graph.parse('tests/examples/dataset.ttl', format='turtle')

    def teadDown(self):
        """Tear down test fixtures."""
        self.graph.destroy()

    def test_parse_metadata_config(self):
        """Test if input output encoding is peformed correctly."""
        result = Graph()
        result = parse_metadata_config(self.config, self.activityId,
                                       self.graph.namespace, self.graph)
        print(str(result.serialize(format='turtle')))
        print(str(self.test_graph.serialize(format='turtle')))

        print(isomorphic(result, self.test_graph))
        # Considering blank nodes we need t check if the graphs are similar
        eq_(similar(result, self.test_graph), True,
            "Test to if the resulting graph corresponds to test graph.")

    def test_namespaces(self):
        """Test for Namespaces."""
        ok_(list(self.graph.namespaces()) != [],
            "Test if there are namespaces.")


if __name__ == "__main__":
    unittest.main()
