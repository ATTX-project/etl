from datetime import datetime
from rdflib.namespace import DC, RDF
from wf_api.utils.logs import app_logger
from rdflib import Graph, URIRef, Literal, Namespace
from wf_api.utils.db import connect_DB, empty_workflows_DB
from wf_api.utils.prefixes import bind_prefix, ATTXBase, ATTXOnto


class WorkflowGraph(object):
    """Create WorkflowGraph class."""

    @classmethod
    def workflow(cls, modifiedSince=None):
        """Build workflow graph with associated information."""
        workflow_graph = Graph()

        # bind prefixes
        bind_prefix(workflow_graph)

        db_cursor = connect_DB()

        test_workflows = cls.fetch_workflows(db_cursor, workflow_graph, modifiedSince)
        if test_workflows == "No workflows":
            db_cursor.connection.close()
            return workflow_graph
        else:
            cls.fetch_steps(db_cursor, workflow_graph)
            cls.fetch_steps_sequence(db_cursor, workflow_graph)
            db_cursor.connection.close()
            return workflow_graph

    @staticmethod
    def fetch_workflows(db_cursor, graph, modifiedSince):
        """Create Workflow ID and description."""
        # Get general workflow information on the last executed workflow
        # Get only public workflows
        db_cursor.execute("""
             SELECT ppl_model.id AS 'workflowId',
             ppl_model.description AS 'description',
             ppl_model.name AS 'workflowTitle',
             ppl_model.last_change AS 'lastChange',
             ppl_model.visibility AS 'visibility'
             FROM ppl_model
             ORDER BY ppl_model.id
        """)
        # replace last line above with one below if only latest result required
        #  ORDER BY ppl_model.last_change DESC LIMIT 1

        result_set = db_cursor.fetchall()
        if db_cursor.rowcount > 0:
            return WorkflowGraph.construct_wf_graph(graph, result_set, modifiedSince)
        else:
            return "No workflows"

    @staticmethod
    def construct_wf_graph(graph, data_row, modifiedSince):
        """Test to see if record has been modifed."""
        for row in data_row:
            last_date = row['lastChange']
            if modifiedSince is None:
                compare_date = None
            else:
                compare_date = datetime.strptime(modifiedSince, '%Y-%m-%dT%H:%M:%SZ')
            if modifiedSince is None or (modifiedSince and last_date >= compare_date):
                graph.add((URIRef("{0}workflow{1}".format(ATTXBase, row['workflowId'])), RDF.type, ATTXOnto.Workflow))
                graph.add((URIRef("{0}workflow{1}".format(ATTXBase, row['workflowId'])), DC.title, Literal(row['workflowTitle'])))
                graph.add((URIRef("{0}workflow{1}".format(ATTXBase, row['workflowId'])), DC.description, Literal(row['description'])))
            else:
                return "No workflows"

    @staticmethod
    def fetch_steps(db_cursor, graph):
        """Create Steps ID and description."""
        # Get steps information
        db_cursor.execute("""
        SELECT dpu_instance.id AS 'stepId', dpu_instance.name AS 'stepTitle',
        dpu_instance.description AS 'description',
        dpu_template.name AS 'templateName', ppl_model.id AS 'workflowId'
        FROM ppl_model, dpu_template, dpu_instance INNER JOIN
        ppl_node ON ppl_node.instance_id=dpu_instance.id
             WHERE ppl_node.graph_id = (
                SELECT id
                FROM ppl_model
                ORDER BY ppl_model.id DESC LIMIT 1)
            AND dpu_instance.dpu_id = dpu_template.id
        """)

        result_set = db_cursor.fetchall()

        PWO = Namespace('http://purl.org/spar/pwo/')

        for row in result_set:
            graph.add((URIRef("{0}step{1}".format(ATTXBase, row['stepId'])), RDF.type, ATTXOnto.Step))
            graph.add((URIRef("{0}step{1}".format(ATTXBase, row['stepId'])), DC.title, Literal(row['stepTitle'])))
            graph.add((URIRef("{0}step{1}".format(ATTXBase, row['stepId'])), DC.description, Literal(row['description'])))
            graph.add((URIRef("{0}workflow{1}".format(ATTXBase, row['workflowId'])), PWO.hasStep, URIRef("{0}step{1}".format(ATTXBase, row['stepId']))))
            app_logger.info('Construct step metadata for Step{0}.' .format(row['stepId']))
        return graph

    @staticmethod
    def fetch_steps_sequence(db_cursor, graph):
        """Create Steps sequence."""
        # Get steps linkage
        db_cursor.execute("""
        SELECT
          FromStep.instance_id AS 'fromStep',
          ToStep.instance_id AS 'toStep'
        FROM ppl_edge
        INNER JOIN ppl_node AS FromStep
          ON FromStep.id=ppl_edge.node_from_id
        INNER JOIN ppl_node AS ToStep
          ON ToStep.id=ppl_edge.node_to_id
        """)

        result_set = db_cursor.fetchall()

        PWO = Namespace('http://purl.org/spar/pwo/')

        for row in result_set:
            graph.add((URIRef("{0}step{1}".format(ATTXBase, row['fromStep'])), PWO.hasNextStep, URIRef("{0}step{1}".format(ATTXBase, row['toStep']))))
            app_logger.info('Fetch steps sequence between steps Step{0} and Step{1}.'.format(row['fromStep'], row['toStep']))
        return graph


def workflow_get_output(serialization, modifiedSince):
    """Construct the Ouput for the Get request."""
    data = WorkflowGraph()
    workflow_graph = data.workflow(modifiedSince)
    if len(workflow_graph) > 0:
        result = workflow_graph.serialize(format=serialization)
    elif len(workflow_graph) == 0:
        if empty_workflows_DB() == 0:
            result = 'Empty'
        else:
            result = None
    app_logger.info('Constructed Output for UnifiedViews Workflow metadata enrichment finalized and set to API.')
    return result
