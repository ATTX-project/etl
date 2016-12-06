import pymysql as mysql
from rdflib import Graph, URIRef, Literal
from rdflib.namespace import DC, RDF
import html
import logging
import logging.config
from configparser import SafeConfigParser

logging.config.fileConfig('logging.conf')
logger = logging.getLogger('workflowLogger')


class WorkflowGraph(object):
    """Create WorkflowGraph class."""

    @classmethod
    def workflow(cls, host_ip, user_name, passwd, db_name):
        """Build workflow graph with associated information."""
        workflow_graph = Graph()
        kaisa_namespace = 'http://helsinki.fi/library/onto#'
        workflow_graph.bind('kaisa', kaisa_namespace)
        workflow_graph.bind('dc', 'http://purl.org/dc/elements/1.1/')
        workflow_graph.bind('schema', 'http://schema.org/')
        workflow_graph.bind('pwo', 'http://purl.org/spar/pwo/')

        try:
            conn = mysql.connect(
                host=host_ip,
                port=3306,
                user=user_name,
                passwd=passwd,
                db=db_name,
                charset='utf8')
        except Exception as error:
            logger.error('Connection Failed!\
                \nError Code is {0};\
                \nError Content is {1};'
                         .format(error.args[0], error.args[1]))

        cls.fetch_workflows(conn, workflow_graph, kaisa_namespace)
        cls.fetch_steps(conn, workflow_graph, kaisa_namespace)
        cls.fetch_steps_sequence(conn, workflow_graph, kaisa_namespace)
        conn.close()
        return workflow_graph

    @staticmethod
    def fetch_workflows(db_connector, graph, namespace):
        """Create Workflow ID and description."""
        cursor = db_connector.cursor(mysql.cursors.DictCursor)

        # Get general workflow information on the last executed workflow
        cursor.execute("""
            SELECT ppl_model.name AS 'workflowId',
            ppl_model.description AS 'description'
            FROM exec_pipeline, ppl_model
            WHERE exec_pipeline.pipeline_id = ppl_model.id
            ORDER BY pipeline_id DESC LIMIT 1
        """)

        result_set = cursor.fetchall()

        for row in result_set:
            graph.add((URIRef("{0}{1}".format(namespace, row['workflowId'])),
                       RDF.type,
                       URIRef("{0}{1}".format(namespace, 'Workflow'))))
            graph.add((URIRef("{0}{1}".format(namespace, row['workflowId'])),
                      DC.title,
                      Literal(row['workflowId'])))
            graph.add((URIRef("{0}{1}".format(namespace, row['workflowId'])),
                      DC.description,
                      Literal(row['description'])))
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
        dpu_template.name AS 'templateName', ppl_model.name AS 'workflowId'
        FROM ppl_model, dpu_template, dpu_instance INNER JOIN
        exec_context_dpu ON exec_context_dpu.dpu_instance_id=dpu_instance.id
             WHERE exec_context_dpu.exec_context_pipeline_id = (
                SELECT id
                FROM exec_pipeline
                ORDER BY id DESC LIMIT 1)
            AND dpu_instance.dpu_id = dpu_template.id
        """)

        result_set = cursor.fetchall()

        for row in result_set:
            graph.add((URIRef("{0}Step{1}".format(namespace, row['stepId'])),
                      RDF.type,
                      URIRef("{0}{1}".format(namespace, 'Step'))))
            graph.add((URIRef("{0}Step{1}".format(namespace, row['stepId'])),
                      DC.title,
                      Literal(row['stepTitle'])))
            graph.add((URIRef("{0}Step{1}".format(namespace, row['stepId'])),
                      DC.description,
                      Literal(row['description'])))
            graph.add((URIRef("{0}Step{1}".format(namespace, row['stepId'])),
                      URIRef('http://schema.org/query'),
                      Literal(html.unescape(str(row['config'], 'UTF-8')))))
            graph.add((URIRef("{0}{1}".format(namespace, row['workflowId'])),
                      URIRef("{0}{1}".format('http://purl.org/spar/pwo/',
                             'hasStep')),
                      URIRef("{0}Step{1}".format(namespace, row['stepId']))))
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

        for row in result_set:
            graph.add((URIRef("{0}Step{1}".format(namespace, row['fromStep'])),
                      URIRef("{0}{1}".format('http://purl.org/spar/pwo/',
                             'hasNextStep')),
                      URIRef("{0}Step{1}".format(namespace, row['toStep']))))
        return graph


def construct_output(serialization=None):
    """Construct the Ouput for the Get request."""
    parser = SafeConfigParser()
    parser.read('database.conf')
    data = WorkflowGraph()
    workflow_graph = data.workflow(
            parser.get('database', 'host'),
            parser.get('database', 'user'),
            parser.get('database', 'passwd'),
            parser.get('database', 'db'))
    if len(workflow_graph) > 0 and serialization is None:
        result = workflow_graph.serialize(format='turtle')
    elif len(workflow_graph) > 0 and serialization is not None:
        result = workflow_graph.serialize(format=serialization)
    elif len(workflow_graph) == 0:
        result = "No new Workflow to be loaded."
    logger.info('Performing UV Workflow metadata extraction and enrichment.')
    return result
