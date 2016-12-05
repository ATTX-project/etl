from rdflib import Graph
from rdflib.serializer import Serializer
import json

def convert(data, output_format=None):
    graph = Graph()
    result = graph.parse(data, format='turtle')
    if output_format == None:
        resp_serialized = graph.serialize(format='turtle')
    else:
        resp_serialized = graph.serialize(format='json-ld', indent=4)
    return resp_serialized
