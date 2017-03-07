import xmltodict
from rdflib import URIRef, Literal
from wf_api.utils.logs import app_logger
from rdflib.namespace import DC, RDF
from wf_api.utils.prefixes import ATTXBase, ATTXOnto, PROV, SD, CC


def parse_metadata_config(config, activityId, graph):
    """Parse metadata specific configuration."""
    metadata_transformer = 'org.uh.hulib.attx.uv.dpu.metadata.Transformer\
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
            input_graph(graph, data, activityId)
            output_graph(graph, data, activityId)
            app_logger.info('Processed activity: {0} '.format(activityId))
        else:
            app_logger.info('Construct config metadata missing information.')
        return graph
    except Exception as error:
        app_logger.error('Something is wrong: {0}'.format(error))
        return error


def input_graph(graph, data, activityId):
    """Input graph is formed."""
    if data['inputGraphURI']:
        graph.add((URIRef(data['inputGraphURI']), RDF.type, ATTXOnto.Dataset))
        graph.add((URIRef(data['inputGraphURI']), RDF.type, SD.Dataset))
        graph.add((URIRef("{0}activity{1}".format(ATTXBase, activityId)), PROV.used, URIRef(data['inputGraphURI'])))
        if data['inputGraphTitle']:
            graph.add((URIRef(data['inputGraphURI']), DC.title, Literal(data['inputGraphTitle'])))
        if data['inputGraphDescription']:
            graph.add((URIRef(data['inputGraphURI']), DC.description, Literal(data['inputGraphDescription'])))
        if data['inputGraphPublisher']:
            graph.add((URIRef(data['inputGraphURI']), DC.publisher, Literal(data['inputGraphPublisher'])))
        if data['inputGraphSource']:
            graph.add((URIRef(data['inputGraphURI']), DC.source, Literal(data['inputGraphSource'])))
        if data['inputGraphLicence']:
            graph.add((URIRef(data['inputGraphURI']), CC.license, URIRef(data['inputGraphLicence'])))
    return graph


def output_graph(graph, data, activityId):
    """Output graph is formed."""
    if data['outputGraphURI']:
        graph.add((URIRef(data['outputGraphURI']), RDF.type, ATTXOnto.Dataset))
        graph.add((URIRef(data['outputGraphURI']), RDF.type, SD.Dataset))
        graph.add((URIRef("{0}activity{1}".format(ATTXBase, activityId)), PROV.generated, URIRef(data['outputGraphURI'])))
        if data['outputGraphTitle']:
            graph.add((URIRef(data['outputGraphURI']), DC.title, Literal(data['outputGraphTitle'])))
        if data['outputGraphDescription']:
            graph.add((URIRef(data['outputGraphURI']), DC.description, Literal(data['outputGraphDescription'])))
        if data['outputGraphPublisher']:
            graph.add((URIRef(data['outputGraphURI']), DC.publisher, Literal(data['outputGraphPublisher'])))
        if data['outputGraphSource']:
            graph.add((URIRef(data['outputGraphURI']), DC.source, Literal(data['outputGraphSource'])))
        if data['outputGraphLicence']:
            graph.add((URIRef(data['outputGraphURI']), CC.license, URIRef(data['outputGraphLicence'])))
    return graph
