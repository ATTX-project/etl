# from wf_api.uv.workflow_metadata import workflow_get_output, WorkflowGraph
# import unittest
# from rdflib import Graph
#
#
# class WorkflowGraphTest(unittest.TestCase):
#     """Test for Activity Response from API."""
#
#     def setUp(self):
#         """Set up test fixtures."""
#         self.graph = Graph()
#         self.format_jsonld = 'json-ld'
#         self.format_turtle = 'turtle'
#         self.data = WorkflowGraph()
#         self.workflow_graph = self.data.workflow('2017-01-17T14:14:14Z')
#
#     def tearDown(self):
#         """Tear down test fixtures."""
#         pass
#
#     def test_workflow_get_output(self):
#         """Test Workflow processing output is Graph."""
#         if workflow_get_output('turtle', None) is not (None or 'Empty'):
#             data_turtle = self.graph.parse(data=str(workflow_get_output(self.format_turtle,
#                                                                         None))
#                                            .encode('utf-8'),
#                                            format=self.format_turtle)
#             data_json = self.graph.parse(data=str(workflow_get_output(self.format_jsonld,
#                                                                       None))
#                                          .encode('utf-8'),
#                                          format=self.format_jsonld)
#             self.assertIsInstance(data_turtle, type(Graph()))
#             self.assertIsInstance(data_json, type(Graph()))
#
#     def test_activity_no_output(self):
#         """Test Activity processing output is empty graph."""
#         if len(self.workflow_graph) == 0:
#             assert True
#
#
# if __name__ == "__main__":
#     unittest.main()
