import pymysql as mysql
from rdflib import Graph, URIRef, Literal, Namespace
from rdflib.namespace import DC, RDF
# import html
import logging
import logging.config
from configparser import SafeConfigParser

logging.config.fileConfig('logging.conf')
logger = logging.getLogger('appLogger')


class WorkflowGraph(object):
    """Create WorkflowGraph class."""

    @classmethod
    def workflow(cls, databaseConfig):
        """Build workflow graph with associated information."""
        workflow_graph = Graph()

        workflow_graph.bind('kaisa', 'http://helsinki.fi/library/onto#')
        workflow_graph.bind('dc', 'http://purl.org/dc/elements/1.1/')
        workflow_graph.bind('schema', 'http://schema.org/')
        workflow_graph.bind('pwo', 'http://purl.org/spar/pwo/')
        workflow_graph.bind('prov', 'http://www.w3.org/ns/prov#')

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

        cls.fetch_workflows(conn, workflow_graph, KAISA)
        cls.fetch_steps(conn, workflow_graph, KAISA)
        cls.fetch_steps_sequence(conn, workflow_graph, KAISA)
        conn.close()
        return workflow_graph

    @staticmethod
    def fetch_workflows(db_connector, graph, namespace):
        """Create Workflow ID and description."""
        cursor = db_connector.cursor(mysql.cursors.DictCursor)

        # Get general workflow information on the last executed workflow
        cursor.execute("""
             SELECT ppl_model.id AS 'workflowId',
             ppl_model.description AS 'description',
             ppl_model.name AS 'workflowTitle'
             FROM ppl_model
             ORDER BY ppl_model.id DESC LIMIT 1
        """)

        result_set = cursor.fetchall()

        for row in result_set:
            graph.add((URIRef("{0}workflow{1}".format(namespace,
                                                      row['workflowId'])),
                       RDF.type,
                      namespace.Workflow))
            graph.add((URIRef("{0}workflow{1}".format(namespace,
                                                      row['workflowId'])),
                      DC.title,
                      Literal(row['workflowTitle'])))
            graph.add((URIRef("{0}workflow{1}".format(namespace,
                                                      row['workflowId'])),
                      DC.description,
                      Literal(row['description'])))
            logger.info('Construct workflow metadata for Workflow{0}.'
                        .format(row['workflowId']))
        return graph

    @staticmethod
    def fetch_steps(db_connector, graph, namespace):
        """Create Steps ID and description."""
        cursor = db_connector.cursor(mysql.cursors.DictCursor)

        # Get steps information along with configuration
        cursor.execute("""
        SELECT dpu_instance.id AS 'stepId', dpu_instance.name AS 'stepTitle',
        dpu_instance.description AS 'description',
        dpu_instance.configuration AS 'config',
        dpu_template.name AS 'templateName', ppl_model.id AS 'workflowId'
        FROM ppl_model, dpu_template, dpu_instance INNER JOIN
        ppl_node ON ppl_node.instance_id=dpu_instance.id
             WHERE ppl_node.graph_id = (
                SELECT id
                FROM ppl_model
                ORDER BY ppl_model.id DESC LIMIT 1)
            AND dpu_instance.dpu_id = dpu_template.id
        """)

        result_set = cursor.fetchall()

        PWO = Namespace('http://purl.org/spar/pwo/')

        for row in result_set:
            graph.add((URIRef("{0}step{1}".format(namespace, row['stepId'])),
                      RDF.type,
                      namespace.Step))
            graph.add((URIRef("{0}step{1}".format(namespace, row['stepId'])),
                      DC.title,
                      Literal(row['stepTitle'])))
            graph.add((URIRef("{0}step{1}".format(namespace, row['stepId'])),
                      DC.description,
                      Literal(row['description'])))
            # Config placement in steps not required
            # graph.add((URIRef("{0}Step{1}".format(namespace, row['stepId'])),
            #           URIRef('http://schema.org/query'),
            #           Literal(html.unescape(str(row['config'], 'UTF-8')))))
            graph.add((URIRef("{0}workflow{1}".format(namespace,
                                                      row['workflowId'])),
                      PWO.hasStep,
                      URIRef("{0}step{1}".format(namespace, row['stepId']))))
            logger.info('Construct step metadata for Step{0}.'
                        .format(row['stepId']))
        return graph

    @staticmethod
    def fetch_steps_sequence(db_connector, graph, namespace):
        """Create Steps sequence."""
        cursor = db_connector.cursor(mysql.cursors.DictCursor)

        # Get steps information along with configuration
        cursor.execute("""
        SELECT
          FromStep.instance_id AS 'fromStep',
          ToStep.instance_id AS 'toStep'
        FROM ppl_edge
        INNER JOIN ppl_node AS FromStep
          ON FromStep.id=ppl_edge.node_from_id
        INNER JOIN ppl_node AS ToStep
          ON ToStep.id=ppl_edge.node_to_id
        """)

        result_set = cursor.fetchall()

        PWO = Namespace('http://purl.org/spar/pwo/')

        for row in result_set:
            graph.add((URIRef("{0}step{1}".format(namespace, row['fromStep'])),
                      PWO.hasNextStep,
                      URIRef("{0}step{1}".format(namespace, row['toStep']))))
            logger.info('Fetch steps sequence between steps Step{0} '
                        'and Step{1}.'.format(row['fromStep'], row['toStep']))
        return graph


def workflow_get_output(serialization=None):
    """Construct the Ouput for the Get request."""
    parser = SafeConfigParser()
    parser.read('database.conf')
    data = WorkflowGraph()
    workflow_graph = data.workflow(parser)
    if len(workflow_graph) > 0 and serialization is None:
        result = workflow_graph.serialize(format='turtle')
    elif len(workflow_graph) > 0 and serialization is not None:
        result = workflow_graph.serialize(format=serialization)
    elif len(workflow_graph) == 0:
        result = "No new Workflow to be loaded."
    logger.info('Constructed Output for UnifiedViews Workflow '
                'metadata enrichment finalized and set to API.')
    return result
