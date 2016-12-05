from api.serialization import convert
import pymysql as mariadb

static_workflow = """
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

kaisa:wf1
  a kaisa:Workflow ;
  dcterms:title "Acquisition and Mapping the VIRTA pubs data." ;
  dcterms:description "Workflow that uses VIRTA publication data in order to map titles and external identifiers for every publication and research fields."@en ;
  pwo:hasFirstStep kaisa:wf1_step1 ;
  pwo:hasStep
    kaisa:wf1_step2 ,
    kaisa:wf1_step1 ,
    kaisa:wf1_step3 ,
    kaisa:wf1_step4 .

kaisa:wf1_step1
  a kaisa:Step ;
  dcterms:title "VIRTA Publication Input Step" ;
  dcterms:description "DPU that gets the latest publication from VIRTA using the API."@en ;
  pwo:hasNextStep kaisa:wf1_step2 .

kaisa:wf1_step2
  a kaisa:Step ;
  dcterms:title "VIRTA Transformation Step" ;
  dcterms:description "DPU that transforms the VIRTA data into RDF format. At the same time it maps titles and external identifiers."@en ;
  pwo:hasNextStep kaisa:wf1_step3 .

kaisa:wf1_step3
  a kaisa:Step ;
  dcterms:title "Adding wf1 Metadata Step" ;
  dcterms:description "DPU that add necessary Metadata, to the specify provenance information."@en ;
  pwo:hasNextStep kaisa:wf1_step4 .

kaisa:wf1_step4
  a kaisa:Step ;
  dcterms:title "VIRTA Publication Output Step" ;
  dcterms:description "DPU that that contains the mapped data and writes it to the a file."@en .
"""

def workflow_get(modifiedSince = None):
    """List the latest workflow and associated steps.

      This operation gathers all the information necessary to describe the workflows, steps and relationships between them
    """
    return static_workflow

def workflow_post():
    """ This operation cannot be Perfomed."""
    return """Operation Not Allowed.""", 405
