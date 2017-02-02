from rdflib import URIRef, Literal, Namespace
from rdflib.namespace import DC, DCTERMS, RDF
import xmltodict
from wf_api.utils.logs import app_logger


def parse_metadata_config(config, activityId, namespace, graph):
    """Parse metadata specific configuration."""
    metadata_transformer = 'org.uh.attx.etl.uv.dpu.transformer.metadata.Transformer\
ATTXMetadataConfig__V1'
    soup = xmltodict.parse(config)
    base = soup["object-stream"]["MasterConfigObject"]["configurations"]["entry"]
    try:
        # this expects that the configuration for the metadata transformer
        # only has one entry
        if len(base) > 1:
            pass
        elif metadata_transformer in base["string"][1]["object-stream"].keys():
            data = base["string"][1]["object-stream"][metadata_transformer]
            input_graph(graph, data, namespace, activityId)
            output_graph(graph, data, namespace, activityId)
        return graph
        app_logger.info('Construct config metadata missing information.')
    except Exception as error:
        return error
        app_logger.error('Something is wrong: {0}'.format(error))


def input_graph(graph, data, namespace, activityId):
    """Input graph is formed."""
    PROV = Namespace('http://www.w3.org/ns/prov#')
    SD = Namespace('http://www.w3.org/ns/sparql-service-description#')

    if data['inputGraphURI']:
        graph.add((URIRef(data['inputGraphURI']),
                  RDF.type,
                  namespace.Dataset))
        graph.add((URIRef(data['inputGraphURI']),
                  RDF.type,
                  SD.Dataset))
        graph.add((URIRef("{0}activity{1}".format(namespace,
                                                  activityId)),
                  PROV.used,
                  URIRef(data['inputGraphURI'])))
        if data['inputGraphTitle']:
            graph.add((URIRef(data['inputGraphURI']),
                      DC.title,
                      Literal(data['inputGraphTitle'])))
        if data['inputGraphDescription']:
            graph.add((URIRef(data['inputGraphURI']),
                      DC.description,
                      Literal(data['inputGraphDescription'])))
        if data['inputGraphPublisher']:
            graph.add((URIRef(data['inputGraphURI']),
                      DC.publisher,
                      Literal(data['inputGraphPublisher'])))
        if data['inputGraphSource']:
            graph.add((URIRef(data['inputGraphURI']),
                      DC.source,
                      Literal(data['inputGraphSource'])))
        if data['inputGraphLicence']:
            graph.add((URIRef(data['inputGraphURI']),
                      DCTERMS.license,
                      Literal(data['inputGraphLicence'])))
    return graph


def output_graph(graph, data, namespace, activityId):
    """Output graph is formed."""
    PROV = Namespace('http://www.w3.org/ns/prov#')
    SD = Namespace('http://www.w3.org/ns/sparql-service-description#')

    if data['outputGraphURI']:
        graph.add((URIRef(data['outputGraphURI']),
                  RDF.type,
                  namespace.Dataset))
        graph.add((URIRef(data['outputGraphURI']),
                  RDF.type,
                  SD.Dataset))
        graph.add((URIRef("{0}activity{1}".format(namespace,
                                                  activityId)),
                  PROV.generated,
                  URIRef(data['outputGraphURI'])))
        if data['outputGraphTitle']:
            graph.add((URIRef(data['outputGraphURI']),
                      DC.title,
                      Literal(data['outputGraphTitle'])))
        if data['outputGraphDescription']:
            graph.add((URIRef(data['outputGraphURI']),
                      DC.description,
                      Literal(data['outputGraphDescription'])))
        if data['outputGraphPublisher']:
            graph.add((URIRef(data['outputGraphURI']),
                      DC.publisher,
                      Literal(data['outputGraphPublisher'])))
        if data['outputGraphSource']:
            graph.add((URIRef(data['outputGraphURI']),
                      DC.source,
                      Literal(data['outputGraphSource'])))
        if data['outputGraphLicence']:
            graph.add((URIRef(data['outputGraphURI']),
                      DCTERMS.license,
                      Literal(data['outputGraphLicence'])))
    return graph
