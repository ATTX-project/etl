from api.serialization import convert
import pymysql as mysql
from flask import Response
from rdflib import Graph, URIRef, Literal, BNode
from rdflib.namespace import DC, RDF
import urllib

# class createWorkflow():
#
#     @staticmethod
#     def databaseSetup():

workflow_graph = Graph()
kaisa_namespace = 'http://helsinki.fi/library/onto#'
workflow_graph.bind('kaisa', kaisa_namespace)
workflow_graph.bind('dc','http://purl.org/dc/elements/1.1/')
workflow_graph.bind('schema', 'http://schema.org/')

try:
    conn = mysql.connect(
        host='localhost',
        port=3306,
        user='unified_views',
        passwd='s00pers3cur3',
        db='unified_views',
        charset='utf8'
    )
except Exception as e:
    print('Connection Failed!\nError Code is %s;\nError Content is %s;' % (e.args[0],e.args[1]))

cursor = conn.cursor(mysql.cursors.DictCursor)

# Get general workflow information on the last executed workflow
cursor.execute("""
    SELECT ppl_model.name AS 'workflowId', ppl_model.description AS 'description'
    FROM exec_pipeline, ppl_model
    WHERE exec_pipeline.pipeline_id = ppl_model.id
    ORDER BY pipeline_id DESC LIMIT 1
""")

result_set1 = cursor.fetchall()

# conn.close()

for row in result_set1:
    workflow_graph.add((URIRef("{0}{1}".format(kaisa_namespace,row['workflowId'])), RDF.type, URIRef("{0}{1}".format(kaisa_namespace,'Workflow')) ))
    workflow_graph.add((URIRef("{0}{1}".format(kaisa_namespace,row['workflowId'])), DC.title,  Literal(row['workflowId']) ))
    workflow_graph.add((URIRef("{0}{1}".format(kaisa_namespace,row['workflowId'])), DC.description,  Literal(row['description']) ))

new_cursor = conn.cursor(mysql.cursors.DictCursor)

#Get steps information along with configuration
new_cursor.execute("""
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

result_set2 = new_cursor.fetchall()

for row in result_set2:
    workflow_graph.add((URIRef("{0}Step{1}".format(kaisa_namespace,row['stepId'])), RDF.type, URIRef("{0}{1}".format(kaisa_namespace,'Step')) ))
    workflow_graph.add((URIRef("{0}Step{1}".format(kaisa_namespace,row['stepId'])), DC.title,  Literal(row['stepTitle']) ))
    workflow_graph.add((URIRef("{0}Step{1}".format(kaisa_namespace,row['stepId'])), DC.description,  Literal(row['description']) ))
    workflow_graph.add((URIRef("{0}Step{1}".format(kaisa_namespace,row['stepId'])), URIRef('http://schema.org/query'),  Literal(row['config'], "utf-8") ))
    workflow_graph.add(( URIRef("{0}{1}".format(kaisa_namespace,row['workflowId'])), URIRef("{0}{1}".format(kaisa_namespace,'hasStep')), URIRef("{0}Step{1}".format(kaisa_namespace,row['stepId'])) ))

def workflow_get(modifiedSince = None):
    """List the latest workflow and associated steps.

      This operation gathers all the information necessary to describe the workflows, steps and relationships between them
    """
    # data = convert(workflow_graph.serialize(format='turtle'))
    the_workflow = Response (
        response = workflow_graph.serialize(format='turtle'),
        status = 200,
        mimetype = 'text/turtle'
    )
    return the_workflow

def workflow_post():
    """ This operation cannot be Perfomed."""
    return """Operation Not Allowed.""", 405
