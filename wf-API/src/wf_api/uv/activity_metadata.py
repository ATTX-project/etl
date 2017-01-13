import HTMLParser
from datetime import datetime
from rdflib import Graph, URIRef, Literal, Namespace, BNode
from rdflib.namespace import RDF, XSD
from wf_api.utils.logs import app_logger
from wf_api.utils.db import connect_DB
from wf_api.utils.prefixes import bind_prefix
from wf_api.uv.parse_config import parse_metadata_config

artifact = 'UnifiedViews'  # Define the ETL agent
agent = 'ETL'  # Define Agent type


class ActivityGraph(object):
    """Create WorkflowGraph class."""

    @classmethod
    def activity(cls, modifiedSince=None):
        """Build activity graph with associated information."""
        activity_graph = Graph()

        bind_prefix(activity_graph)
        KAISA = Namespace('http://helsinki.fi/library/onto#')

        db_cursor = connect_DB()

        test_activities = cls.fetch_activities(db_cursor, activity_graph,
                                               KAISA, modifiedSince)
        if test_activities == "No workflows":
            db_cursor.connection.close()
            return activity_graph
        else:
            cls.fetch_metadata(db_cursor, activity_graph, KAISA)
            db_cursor.connection.close()
            return activity_graph

    @staticmethod
    def fetch_activities(db_cursor, graph, namespace, modifiedSince):
        """Create activity ID and description."""
        # Get general workflow information on the last executed workflow
        # Get based only on public workflows and successful pipeline execution
        db_cursor.execute("""
            SELECT exec_pipeline.id AS 'activityId',
            ppl_model.id AS 'workflowId',
            exec_pipeline.t_start AS 'activityStart',
            exec_pipeline.t_end AS 'activityEnd',
            exec_pipeline.t_end AS 'lastChange'
            FROM exec_pipeline, ppl_model
            WHERE exec_pipeline.pipeline_id = ppl_model.id AND\
            (ppl_model.visibility = 1 OR ppl_model.visibility = 2) AND\
            exec_pipeline.status = 5
            ORDER BY ppl_model.id
        """)
        # replace last line above with one below if only latest result required
        #  ORDER BY ppl_model.last_change DESC LIMIT 1
        result_set = db_cursor.fetchall()

        if db_cursor.rowcount > 0:
            return ActivityGraph.construct_act_graph(graph, result_set,
                                                     namespace,
                                                     modifiedSince)
        else:
            return "No activities"

    @staticmethod
    def construct_act_graph(graph, data_row, namespace, modifiedSince):
        """Test to see if record has been modifed."""
        PROV = Namespace('http://www.w3.org/ns/prov#')
        for row in data_row:
            old_date = row['lastChange']
            if modifiedSince is None:
                new_date = None
            else:
                new_date = datetime.strptime(modifiedSince,
                                             '%Y-%m-%dT%H:%M:%SZ')
            if modifiedSince is None or (modifiedSince
                                         and old_date >= new_date):
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
                          Literal(row['activityStart'],
                                  datatype=XSD.dateTime)))
                graph.add((URIRef("{0}activity{1}".format(namespace,
                                                          row['activityId'])),
                          PROV.endedAtTime,
                          Literal(row['activityEnd'], datatype=XSD.dateTime)))
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
                # information about the agent and the artifact used.
                graph.add((URIRef("{0}{1}".format(namespace, agent)),
                          namespace.usesArtifact,
                          URIRef("{0}{1}".format(namespace, artifact))))
                app_logger.info('Construct activity metadata for Activity{0}.'
                                .format(row['activityId']))
            else:
                return "No activities"

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
            parse_metadata_config(HTMLParser.HTMLParser().unescape(
                                  str(row['config'])),
                                  row['activityId'], namespace, graph)
            app_logger.info('Construct config metadata for Activity{0}.'
                            .format(row['activityId']))
        return graph


def activity_get_output(serialization, modifiedSince):
    """Construct the Ouput for the Get request."""
    data = ActivityGraph()
    activity_graph = data.activity(modifiedSince)
    if len(activity_graph) > 0:
        result = activity_graph.serialize(format=serialization)
    elif len(activity_graph) == 0:
        result = None
    app_logger.info('Constructed Output for UnifiedViews Activity '
                    'metadata enrichment finalized and set to API.')
    return result
