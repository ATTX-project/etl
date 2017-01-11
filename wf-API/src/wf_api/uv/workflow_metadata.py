from datetime import datetime
from rdflib import Graph, URIRef, Literal, Namespace
from rdflib.namespace import DC, RDF
from wf_api.utils.logs import app_logger
from wf_api.utils.db import connect_DB
from wf_api.utils.prefixes import bind_prefix


class WorkflowGraph(object):
    """Create WorkflowGraph class."""

    @classmethod
    def workflow(cls, modifiedSince=None):
        """Build workflow graph with associated information."""
        workflow_graph = Graph()

        # bind prefixes
        bind_prefix(workflow_graph)
        KAISA = Namespace('http://helsinki.fi/library/onto#')

        db_cursor = connect_DB()

        test_workflows = cls.fetch_workflows(db_cursor, workflow_graph, KAISA,
                                             modifiedSince)
        if test_workflows == "No workflows":
            db_cursor.connection.close()
            return workflow_graph
        else:
            cls.fetch_steps(db_cursor, workflow_graph, KAISA)
            cls.fetch_steps_sequence(db_cursor, workflow_graph, KAISA)
            db_cursor.connection.close()
            return workflow_graph

    @staticmethod
    def fetch_workflows(db_cursor, graph, namespace, modifiedSince):
        """Create Workflow ID and description."""
        # Get general workflow information on the last executed workflow
        # Get only public workflows
        db_cursor.execute("""
             SELECT ppl_model.id AS 'workflowId',
             ppl_model.description AS 'description',
             ppl_model.name AS 'workflowTitle',
             ppl_model.last_change AS 'lastChange'
             FROM ppl_model
             WHERE (ppl_model.visibility = 1 OR ppl_model.visibility = 2)
             ORDER BY ppl_model.id
        """)
        # replace last line above with one below if only latest result required
        #  ORDER BY ppl_model.last_change DESC LIMIT 1

        result_set = db_cursor.fetchall()
        if db_cursor.rowcount > 0:
            return WorkflowGraph.construct_wf_graph(graph, result_set,
                                                    namespace, modifiedSince)
        else:
            return "No workflows"

    @staticmethod
    def construct_wf_graph(graph, data_row, namespace, modifiedSince):
        """Test to see if record has been modifed."""
        for row in data_row:
            last_date = row['lastChange']
            if modifiedSince is None:
                compare_date = None
            else:
                compare_date = datetime.strptime(modifiedSince,
                                                 '%Y-%m-%dT%H:%M:%SZ')
            if modifiedSince is None or (modifiedSince
                                         and last_date >= compare_date):
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

                return graph
            else:
                return "No workflows"

    @staticmethod
    def fetch_steps(db_cursor, graph, namespace):
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
            graph.add((URIRef("{0}step{1}".format(namespace, row['stepId'])),
                      RDF.type,
                      namespace.Step))
            graph.add((URIRef("{0}step{1}".format(namespace, row['stepId'])),
                      DC.title,
                      Literal(row['stepTitle'])))
            graph.add((URIRef("{0}step{1}".format(namespace, row['stepId'])),
                      DC.description,
                      Literal(row['description'])))
            graph.add((URIRef("{0}workflow{1}".format(namespace,
                                                      row['workflowId'])),
                      PWO.hasStep,
                      URIRef("{0}step{1}".format(namespace, row['stepId']))))
            app_logger.info('Construct step metadata for Step{0}.'
                            .format(row['stepId']))
        return graph

    @staticmethod
    def fetch_steps_sequence(db_cursor, graph, namespace):
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
            graph.add((URIRef("{0}step{1}".format(namespace, row['fromStep'])),
                      PWO.hasNextStep,
                      URIRef("{0}step{1}".format(namespace, row['toStep']))))
            app_logger.info('Fetch steps sequence between steps Step{0} '
                            'and Step{1}.'.format(row['fromStep'],
                                                  row['toStep']))
        return graph


def workflow_get_output(serialization, modifiedSince):
    """Construct the Ouput for the Get request."""
    data = WorkflowGraph()
    workflow_graph = data.workflow(modifiedSince)
    if len(workflow_graph) > 0:
        result = workflow_graph.serialize(format=serialization)
    elif len(workflow_graph) == 0:
        result = None
    app_logger.info('Constructed Output for UnifiedViews Workflow '
                    'metadata enrichment finalized and set to API.')
    return result
