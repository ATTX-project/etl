import rdflib
from flask import make_response, Flask

def convert(data, output_format=None):
    graph = rdflib.Graph()
    result = graph.parse(data)
    if output_format == None:
        resp_serialized = graph.serialize(format='turtle')
    else:
        resp_serialized = graph.serialize(format='json-ld')
    return resp_serialized
