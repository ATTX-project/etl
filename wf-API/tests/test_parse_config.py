from wf_api.uv.parse_config import parse_metadata_config
from rdflib import Graph, Namespace
from rdflib.compare import similar, isomorphic
import unittest
import HTMLParser


class ParseConfigTest(unittest.TestCase):
    """Test for Prasing UnifiedViews plugin specific configuration."""

    def setUp(self):
        """Set up test fixtures."""
        self.graph = Graph()
        self.test_graph = Graph()
        self.blank_graph = Graph()
        self.activityId = '1'
        self.graph.namespace = Namespace('http://data.hulib.helsinki.fi/attx/')
        self.graph.bind('attx', 'http://data.hulib.helsinki.fi/attx/')
        self.graph.bind('attxonto', 'http://data.hulib.helsinki.fi/attx/onto#')
        self.graph.bind('dc', 'http://purl.org/dc/elements/1.1/')
        self.graph.bind('schema', 'http://schema.org/')
        self.graph.bind('pwo', 'http://purl.org/spar/pwo/')
        self.graph.bind('prov', 'http://www.w3.org/ns/prov#')
        self.graph.bind('dcterms', 'http://purl.org/dc/terms/')
        self.graph.bind('sd', 'http://www.w3.org/ns/sparql-service-description#')

        # Tests are run from top directory
        self.config = open('tests/resources/config.xml', 'r')
        with open('tests/resources/config_sparql.xml', 'r') as sparqlfile:
            self.config_sparql = sparqlfile.read().replace('\n', '')
        with open('tests/resources/config_other.xml', 'r') as oytherfile:
            self.config_other = oytherfile.read().replace('\n', '')
        self.test_graph.parse('tests/resources/dataset.ttl', format='turtle')
        self.blank_graph.parse('tests/resources/blank.ttl', format='turtle')

    def tearDown(self):
        """Tear down test fixtures."""
        pass

    def test_parse_metadata_config(self):
        """Test if input output encoding is peformed correctly."""
        result = parse_metadata_config(self.config, self.activityId, self.graph)

        # Considering blank nodes we need to check if the graphs are similar
        assert(isomorphic(result, self.test_graph) is True)

    def test_parse_metadata_config_sparql(self):
        """Test if input output encoding is peformed correctly."""
        result = Graph()
        parsed = HTMLParser.HTMLParser().unescape(self.config_sparql)
        result = parse_metadata_config(parsed, self.activityId, self.graph)

        # Considering blank nodes we need to check if the graphs are similar
        assert(similar(result, self.blank_graph) is True)

    def test_parse_metadata_config_other(self):
        """Test if input output encoding is peformed correctly."""
        result = Graph()
        parsed = HTMLParser.HTMLParser().unescape(self.config_other)
        result = parse_metadata_config(parsed, self.activityId, self.graph)

        # Considering blank nodes we need to check if the graphs are similar
        assert(similar(result, self.blank_graph) is True)

    def test_namespaces(self):
        """Test for Namespaces."""
        self.assertTrue(list(self.graph.namespaces()) != [])


if __name__ == "__main__":
    unittest.main()
