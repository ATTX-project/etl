from api.serialization import convert
import pymysql as mariadb


static_activity = """
@base <http://helsinki.fi/library/> .
@prefix kaisa: <http://helsinki.fi/library/onto#> .
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix xml: <http://www.w3.org/XML/1998/namespace> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix prov: <http://www.w3.org/ns/prov#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix dcterms: <http://purl.org/dc/terms/> .
@prefix sd: <http://www.w3.org/ns/sparql-service-description#> .
@prefix pwo: <http://purl.org/spar/pwo/> .
kaisa:wf2_dataset1
  a kaisa:Dataset, sd:Dataset ;
  dcterms:title "VIRTA Publication Dataset" ;
  dcterms:description "National list of publication that are part of the official VIRTA collection."@en;
  dcterms:publisher "Ministry of Culture and Education" ;
  dcterms:source <http://virta.fi/data_dump.csv> ; # actual source is a CSV file.
  dcterms:license <http://creativecommons.org/licenses/by/4.0/> .

kaisa:wf2_dataset2
  a kaisa:Dataset, sd:Dataset ;
  dcterms:title "CRIS Publication Dataset" ;
  dcterms:description "National list of publication that are part of the official CRIS collection."@en;
  dcterms:publisher "Ministry of Culture and Education" ;
  dcterms:source <http://cris.fi/data_dump.csv> ; # actual source is a CSV file.
  dcterms:license <http://creativecommons.org/licenses/by/4.0/> .

kaisa:wf2_dataset3
  a kaisa:Dataset, sd:Dataset ;
  dcterms:title "Selected VIRTA Publication Data in RDF" ;
  dcterms:description "Dataset includes titles and external identifiers for every publication and research fields, see mapping steps for details"@en;
  dcterms:license <http://creativecommons.org/licenses/by/4.0/> . # license should be the same as original data

kaisa:activity2
  a prov:Activity , kaisa:WorkflowExecution ;
  prov:startedAtTime  "2016-11-17T13:02:10+02:00"^^xsd:dateTime ;
  prov:endedAtTime "2016-11-17T13:40:47+02:00"^^xsd:dateTime ;
  prov:qualifiedAssociation [
    a prov:Assocation ;
    prov:agent kaisa:ETL ;
    prov:hadPlan kaisa:wf2 ;
  ] ;
  prov:used kaisa:wf2_dataset1 ;
  prov:used kaisa:wf2_dataset2 ;
  prov:generated kaisa:wf2_dataset3 .
"""

try:
    conn = mariadb.connect(
        host='localhost',
        port=3306,
        user='unified_views',
        passwd='s00pers3cur3',
        db='unified_views',
        charset='utf8'
    )
except Exception as e:
    print('Connection Failed!\nError Code is %s;\nError Content is %s;' % (e.args[0],e.args[1]))

def activity_get(modifiedSince = None):
    """Retrieve the latest activity and associated datasets.

    This operation gathers all the information necessary to describe the activities, datasets and how the datasets are acquired (input dataset) or produced (output dataset).
    """
    data = convert(static_activity, 'json-ld')
    print(data)
    return static_activity

def activity_post():
    """ This operation cannot be Perfomed."""
    return """Operation Not Allowed.""", 405
