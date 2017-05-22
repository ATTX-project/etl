# from wf_api.api.activity import activity_get, activity_post
# import unittest
# from wf_api.app import api_version, wfm_app
# from rdflib import Graph
# from flask import Response
# import httpretty
#
#
# class ActivityResponseTest(unittest.TestCase):
#     """Test for Activity Response from API."""
#
#     def setUp(self):
#         """Set up test fixtures."""
#         self.app = wfm_app.app.test_client()
#         # propagate the exceptions to the test client
#         self.app.testing = True
#
#         self.graph = Graph()
#
#     def tearDown(self):
#         """Tear down test fixtures."""
#         pass
#
#     @httpretty.activate
#     def test_activity_post_response(self):
#         """Test Activity POST Endpoint responds with a status code."""
#         httpretty.register_uri(httpretty.POST, "http://localhost:4301/0.1/activity", status=405)
#         result = self.app.post('/{0}/activity'.format(api_version))
#
#         # assert the status code of the response
#         assert(result.status_code == 405)
#
#     @httpretty.activate
#     def test_activity_get_response(self):
#         """Test Activity GET Endpoint responds with a status code."""
#         httpretty.register_uri(httpretty.GET, "http://localhost:4301/0.1/activity", status=200)
#         result = self.app.get('/{0}/activity'.format(api_version))
#
#         # assert the status code of the response
#         self.assertIn(result.status_code, [200, 204, 304, 405])
#
#     def test_activity_post_data(self):
#         """Test Activity POST Endpoint data response."""
#         result = self.app.post('/{0}/activity'.format(api_version))
#
#         # assert the status code of the response
#         assert(str(result.data).encode('utf-8') == """Operation Not Allowed.""")
#
#     def test_activity_get_jsondata(self):
#         """Test Activity GET Endpoint JSON-LD data response."""
#         result = self.app.get('/{0}/activity?format=json-ld'.format(api_version))
#
#         if result.status_code == 200:
#             data = self.graph.parse(data=str(result.data).encode('utf-8'),
#                                     format='json-ld')
#             self.assertIsInstance(data, type(Graph()))
#         elif result.status_code == 204:
#             assert(result.data == '')
#         elif result.status_code == 304:
#             assert(result.data is None)
#
#     def test_activity_get_modified(self):
#         """Test Activity GET Endpoint JSON-LD data response."""
#         parameters = '?modifiedSince=2017-01-03T08%3A14%3A14Z'
#         result = self.app.get('/{0}/activity{1}'.format(api_version, parameters))
#
#         if result.status_code == 200:
#             data = self.graph.parse(data=str(result.data).encode('utf-8'),
#                                     format='turtle')
#             self.assertIsInstance(data, type(Graph()))
#         elif result.status_code == 204:
#             assert(result.data == '')
#         elif result.status_code == 304:
#             assert(result.data is None)
#
#     def test_activity_get_json_and_date(self):
#         """Test Activity GET Endpoint JSON-LD + modifiedSince data response."""
#         parameters = '?modifiedSince=2017-01-03T08%3A14%3A14Z&format=json-ld'
#         result = self.app.get('/{0}/activity{1}'.format(api_version, parameters))
#
#         if result.status_code == 200:
#             data = self.graph.parse(data=str(result.data).encode('utf-8'),
#                                     format='json-ld')
#             self.assertIsInstance(data, type(Graph()))
#         elif result.status_code == 204:
#             assert(result.data == '')
#         elif result.status_code == 304:
#             assert(result.data is None)
#
#     def test_activity_get_data(self):
#         """Test Activity GET Endpoint data response."""
#         result = self.app.get('/{0}/activity'.format(api_version))
#
#         if result.status_code == 200:
#             data = self.graph.parse(data=str(result.data).encode('utf-8'),
#                                     format='turtle')
#             self.assertIsInstance(data, type(Graph()))
#         elif result.status_code == 204:
#             assert(result.data == '')
#         elif result.status_code == 304:
#             assert(result.data is None)
#
#     def test_activity_get_badformat(self):
#         """Test Activity GET Endpoint bad format data response."""
#         result = self.app.get('/{0}/activity?format=trig'.format(api_version))
#         assert(result.status_code == 405)
#
#     def test_activity_get(self):
#         """Test Activity GET provides a proper Response type."""
#         self.assertIsInstance(activity_get(), type(Response()))
#
#     def test_activity_post(self):
#         """Test Activity POST provides a proper Response type."""
#         self.assertIsInstance(activity_post(), type(Response()))
#
#
# if __name__ == "__main__":
#     unittest.main()
