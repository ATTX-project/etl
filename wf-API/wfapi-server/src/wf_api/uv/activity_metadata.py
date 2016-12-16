from html import unescape
from rdflib import Graph, URIRef, Literal, Namespace, BNode
from rdflib.namespace import RDF, XSD
from logs import app_logger
from utils.db import connect_DB
from utils.prefixes import bind_prefix
from uv.parse_config import parse_metadata_config

artifact = 'UnifiedViews'  # Define the ETL agent
agent = 'ETL'  # Define Agent type


class ActivityGraph(object):
    """Create WorkflowGraph class."""

    @classmethod
    def activity(cls, db_conf=None, namespace_conf=None):
        """Build activity graph with associated information."""
        activity_graph = Graph()

        bind_prefix(activity_graph)
        KAISA = Namespace('http://helsinki.fi/library/onto#')

        db_cursor = connect_DB()

        cls.fetch_activities(db_cursor, activity_graph, KAISA)
        cls.fetch_metadata(db_cursor, activity_graph, KAISA)
        db_cursor.connection.close()
        return activity_graph

    @staticmethod
    def fetch_activities(db_cursor, graph, namespace):
        """Create activity ID and description."""
        # Get general workflow information on the last executed workflow
        db_cursor.execute("""
            SELECT ppl_model.id AS 'workflowId',
            exec_pipeline.id AS 'activityId',
            exec_pipeline.t_start AS 'activityStart',
            exec_pipeline.t_end AS 'activityEnd'
            FROM exec_pipeline, ppl_model
            WHERE exec_pipeline.pipeline_id = ppl_model.id
            ORDER BY ppl_model.id DESC LIMIT 1
        """)

        result_set = db_cursor.fetchall()

        PROV = Namespace('http://www.w3.org/ns/prov#')

        for row in result_set:
            bnode = BNode()
            graph.add((URIRef("{0}activity{1}".format(namespace,
                                                      row['activityId'])),
                       RDF.type,
                      PROV.Activity))
            graph.add((URIRef("{0}activity{1}".format(namespace,
                                                      row['activityId'])),
                       RDF.type,
                      namespace.WorkflowExecution))
            graph.add((URIRef("{0}activity{1}".format(namespace,
                                                      row['activityId'])),
                      PROV.startedAtTime,
                      Literal(row['activityStart'], datatype=XSD.date)))
            graph.add((URIRef("{0}activity{1}".format(namespace,
                                                      row['activityId'])),
                      PROV.endedAtTime,
                      Literal(row['activityEnd'], datatype=XSD.date)))
            graph.add((URIRef("{0}activity{1}".format(namespace,
                                                      row['activityId'])),
                      PROV.qualifiedAssociation,
                      bnode))
            graph.add((bnode,
                      RDF.type,
                      PROV.Assocation))
            graph.add((bnode,
                      PROV.hadPlan,
                      URIRef("{0}workflow{1}".format(namespace,
                                                     row['workflowId']))))
            graph.add((bnode,
                      PROV.agent,
                      URIRef("{0}{1}".format(namespace, agent))))
            graph.add((URIRef("{0}{1}".format(namespace, agent)),
                      RDF.type,
                      PROV.Agent))
            graph.add((URIRef("{0}{1}".format(namespace, agent)),
                      namespace.usesArtifact,
                      URIRef("{0}{1}".format(namespace, artifact))))
            app_logger.info('Construct activity metadata for Activity{0}.'
                            .format(row['activityId']))
        return graph

    @staticmethod
    def fetch_metadata(db_cursor, graph, namespace):
        """Create Datasets ID and description."""
        # Get steps configuration
        db_cursor.execute("""
        SELECT dpu_instance.configuration AS 'config',
        exec_pipeline.id AS 'activityId'
        FROM exec_pipeline, dpu_instance INNER JOIN
        ppl_node ON ppl_node.instance_id=dpu_instance.id
             WHERE ppl_node.graph_id = (
                SELECT id
                FROM ppl_model
                ORDER BY ppl_model.id DESC LIMIT 1)
        """)

        result_set = db_cursor.fetchall()

        for row in result_set:
            parse_metadata_config(unescape(str(row['config'], 'UTF-8')),
                                  row['activityId'], namespace, graph)
            app_logger.info('Construct config metadata for Activity{0}.'
                            .format(row['activityId']))
        return graph


def activity_get_output(serialization=None):
    """Construct the Ouput for the Get request."""
    data = ActivityGraph()
    activity_graph = data.activity()
    if len(activity_graph) > 0 and serialization is None:
        result = activity_graph.serialize(format='turtle')
    elif len(activity_graph) > 0 and serialization is not None:
        result = activity_graph.serialize(format=serialization)
    elif len(activity_graph) == 0:
        print(activity_graph)
        result = "No Activity to be loaded."
    app_logger.info('Constructed Output for UnifiedViews Activity '
                    'metadata enrichment finalized and set to API.')
    return result
