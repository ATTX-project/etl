from rdflib import URIRef, Literal, Namespace
from rdflib.namespace import DC, DCTERMS, RDF
from bs4 import BeautifulSoup
from wf_api.utils.logs import app_logger

metadata_transformer = 'org.uh.attx.etl.uv.dpu.transformer.metadata.Transformer\
ATTXMetadataConfig__V1'


def parse_metadata_config(config, activityId, namespace, graph):
    """Parse metadata specific configuration."""
    soup = BeautifulSoup(config, 'lxml-xml')
    # If the date in the input and ouput graphs will be empty
    # it will not return any dataset information.
    if soup.find(metadata_transformer):
        input_graph(graph, soup, namespace, activityId)
        output_graph(graph, soup, namespace, activityId)
        if soup.inputGraphURI.get_text() and soup.outputGraphURI.get_text():
            app_logger.info('Construct config metadata for InputGraph: {0}.'
                            'and OutputGraph: {1}'
                            .format(soup.inputGraphURI.get_text(),
                                    soup.outputGraphURI.get_text()))
        else:
            app_logger.info('Construct config metadata missing information.')
    return graph


def input_graph(graph, soup, namespace, activityId):
    """Input graph is formed."""
    PROV = Namespace('http://www.w3.org/ns/prov#')
    SD = Namespace('http://www.w3.org/ns/sparql-service-description#')

    if soup.inputGraphURI.get_text():
        graph.add((URIRef(soup.inputGraphURI.get_text()),
                  RDF.type,
                  namespace.Dataset))
        graph.add((URIRef(soup.inputGraphURI.get_text()),
                  RDF.type,
                  SD.Dataset))
        graph.add((URIRef("{0}activity{1}".format(namespace,
                                                  activityId)),
                  PROV.used,
                  URIRef(soup.inputGraphURI.get_text())))
        if soup.inputGraphTitle.get_text():
            graph.add((URIRef(soup.inputGraphURI.get_text()),
                      DC.title,
                      Literal(soup.inputGraphTitle.get_text())))
        if soup.inputGraphDescription.get_text():
            graph.add((URIRef(soup.inputGraphURI.get_text()),
                      DC.description,
                      Literal(soup.inputGraphDescription.get_text())))
        if soup.inputGraphPublisher.get_text():
            graph.add((URIRef(soup.inputGraphURI.get_text()),
                      DC.publisher,
                      Literal(soup.inputGraphPublisher.get_text())))
        if soup.inputGraphSource.get_text():
            graph.add((URIRef(soup.inputGraphURI.get_text()),
                      DC.source,
                      Literal(soup.inputGraphSource.get_text())))
        if soup.inputGraphLicence.get_text():
            graph.add((URIRef(soup.inputGraphURI.get_text()),
                      DCTERMS.license,
                      Literal(soup.inputGraphLicence.get_text())))
    return graph


def output_graph(graph, soup, namespace, activityId):
    """Output graph is formed."""
    PROV = Namespace('http://www.w3.org/ns/prov#')
    SD = Namespace('http://www.w3.org/ns/sparql-service-description#')

    if soup.outputGraphURI.get_text():
        graph.add((URIRef(soup.outputGraphURI.get_text()),
                  RDF.type,
                  namespace.Dataset))
        graph.add((URIRef(soup.outputGraphURI.get_text()),
                  RDF.type,
                  SD.Dataset))
        graph.add((URIRef("{0}activity{1}".format(namespace,
                                                  activityId)),
                  PROV.generated,
                  URIRef(soup.outputGraphURI.get_text())))
        if soup.outputGraphTitle.get_text():
            graph.add((URIRef(soup.outputGraphURI.get_text()),
                      DC.title,
                      Literal(soup.outputGraphTitle.get_text())))
        if soup.outputGraphDescription.get_text():
            graph.add((URIRef(soup.outputGraphURI.get_text()),
                      DC.description,
                      Literal(soup.outputGraphDescription.get_text())))
        if soup.outputGraphPublisher.get_text():
            graph.add((URIRef(soup.outputGraphURI.get_text()),
                      DC.publisher,
                      Literal(soup.outputGraphPublisher.get_text())))
        if soup.outputGraphSource.get_text():
            graph.add((URIRef(soup.outputGraphURI.get_text()),
                      DC.source,
                      Literal(soup.outputGraphSource.get_text())))
        if soup.outputGraphLicence.get_text():
            graph.add((URIRef(soup.outputGraphURI.get_text()),
                      DCTERMS.license,
                      Literal(soup.outputGraphLicence.get_text())))
    return graph
