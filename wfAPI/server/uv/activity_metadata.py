import pymysql as mysql
from rdflib import Graph, URIRef, Literal, Namespace, BNode
from rdflib.namespace import DC, RDF, XSD
import html
import logging
import logging.config
from configparser import SafeConfigParser
from uv.parse_config import parse_metadata_config

logging.config.fileConfig('logging.conf')
logger = logging.getLogger('appLogger')

artifact = 'UnifiedViews'  # Define the ETL agent
agent = 'ETL'  # Define Agent type


class ActivityGraph(object):
    """Create WorkflowGraph class."""

    @classmethod
    def activity(cls, databaseConfig):
        """Build activity graph with associated information."""
        activity_graph = Graph()

        activity_graph.bind('kaisa', 'http://helsinki.fi/library/onto#')
        activity_graph.bind('dc', 'http://purl.org/dc/elements/1.1/')
        activity_graph.bind('dcterms', 'http://purl.org/dc/terms/')
        activity_graph.bind('schema', 'http://schema.org/')
        activity_graph.bind('pwo', 'http://purl.org/spar/pwo/')
        activity_graph.bind('prov', 'http://www.w3.org/ns/prov#')
        activity_graph.bind('sd',
                            'http://www.w3.org/ns/sparql-service-description#')

        KAISA = Namespace('http://helsinki.fi/library/onto#')

        try:
            conn = mysql.connect(
                host=databaseConfig.get('uv_database', 'host'),
                port=3306,
                user=databaseConfig.get('uv_database', 'user'),
                passwd=databaseConfig.get('uv_database', 'passwd'),
                db=databaseConfig.get('uv_database', 'db'),
                charset='utf8')
            logger.info('Connecting to database.')
        except Exception as error:
            logger.error('Connection Failed!\
                \nError Code is {0};\
                \nError Content is {1};'
                         .format(error.args[0], error.args[1]))

        cls.fetch_activities(conn, activity_graph, KAISA)
        cls.fetch_metadata(conn, activity_graph, KAISA)
        conn.close()
        return activity_graph

    @staticmethod
    def fetch_activities(db_connector, graph, namespace):
        """Create activity ID and description."""
        cursor = db_connector.cursor(mysql.cursors.DictCursor)

        # Get general workflow information on the last executed workflow
        cursor.execute("""
            SELECT ppl_model.id AS 'workflowId',
            exec_pipeline.id AS 'activityId',
            exec_pipeline.t_start AS 'activityStart',
            exec_pipeline.t_end AS 'activityEnd'
            FROM exec_pipeline, ppl_model
            WHERE exec_pipeline.pipeline_id = ppl_model.id
            ORDER BY ppl_model.id DESC LIMIT 1
        """)

        result_set = cursor.fetchall()

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
            logger.info('Construct activity metadata for Activity{0}.'
                        .format(row['activityId']))
        return graph

    @staticmethod
    def fetch_metadata(db_connector, graph, namespace):
        """Create Datasets ID and description."""
        cursor = db_connector.cursor(mysql.cursors.DictCursor)

        # Get steps information along with configuration
        cursor.execute("""
        SELECT dpu_instance.configuration AS 'config',
        exec_pipeline.id AS 'activityId'
        FROM exec_pipeline, dpu_instance INNER JOIN
        ppl_node ON ppl_node.instance_id=dpu_instance.id
             WHERE ppl_node.graph_id = (
                SELECT id
                FROM ppl_model
                ORDER BY ppl_model.id DESC LIMIT 1)
        """)

        result_set = cursor.fetchall()

        for row in result_set:
            parse_metadata_config(html.unescape(str(row['config'],
                                                'UTF-8')),
                                  row['activityId'], namespace, graph)
            logger.info('Construct activity config metadata for Activity{0}.'
                        .format(row['activityId']))
        return graph


def construct_output(serialization):
    """Construct the Ouput for the Get request."""
    parser = SafeConfigParser()
    parser.read('database.conf')
    data = ActivityGraph()
    activity_graph = data.activity(parser)
    result = activity_graph.serialize(format='turtle')
    logger.info('Constructed Output for UnifiedViews Activity '
                'metadata enrichment finalized and set to API.')
    return result
