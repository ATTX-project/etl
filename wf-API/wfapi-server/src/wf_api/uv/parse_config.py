from rdflib import URIRef, Literal, Namespace
from rdflib.namespace import DC, DCTERMS, RDF
from bs4 import BeautifulSoup
from logs import app_logger

metadata_transformer = 'org.uh.attx.etl.uv.dpu.transformer.metadata.Transformer\
ATTXMetadataConfig__V1'


def parse_metadata_config(config, activityId, namespace, graph):
    """Parse metadata specific configuration."""
    soup = BeautifulSoup(config, 'lxml-xml')

    PROV = Namespace('http://www.w3.org/ns/prov#')
    SD = Namespace('http://www.w3.org/ns/sparql-service-description#')

    if soup.find(metadata_transformer):
        # InputGraph
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
        graph.add((URIRef(soup.inputGraphURI.get_text()),
                  DC.title,
                  Literal(soup.inputGraphTitle.get_text())))
        graph.add((URIRef(soup.inputGraphURI.get_text()),
                  DC.description,
                  Literal(soup.inputGraphDescription.get_text())))
        graph.add((URIRef(soup.inputGraphURI.get_text()),
                  DC.publisher,
                  Literal(soup.inputGraphPublisher.get_text())))
        graph.add((URIRef(soup.inputGraphURI.get_text()),
                  DC.source,
                  Literal(soup.inputGraphSource.get_text())))
        graph.add((URIRef(soup.inputGraphURI.get_text()),
                  DCTERMS.license,
                  Literal(soup.inputGraphLicence.get_text())))
        # OutputGraph
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
        graph.add((URIRef(soup.outputGraphURI.get_text()),
                  DC.title,
                  Literal(soup.outputGraphTitle.get_text())))
        graph.add((URIRef(soup.outputGraphURI.get_text()),
                  DC.description,
                  Literal(soup.outputGraphDescription.get_text())))
        graph.add((URIRef(soup.outputGraphURI.get_text()),
                  DC.publisher,
                  Literal(soup.outputGraphPublisher.get_text())))
        graph.add((URIRef(soup.outputGraphURI.get_text()),
                  DC.source,
                  Literal(soup.inputGraphSource.get_text())))
        graph.add((URIRef(soup.outputGraphURI.get_text()),
                  DCTERMS.license,
                  Literal(soup.outputGraphLicence.get_text())))
        app_logger.info('Construct config metadata for InputGraph: {0}.'
                        'and OutputGraph: {1}'
                        .format(soup.inputGraphURI.get_text(),
                                soup.outputGraphURI.get_text()))
    return graph
