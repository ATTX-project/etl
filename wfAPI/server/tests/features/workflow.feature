Feature: Handle retrieving of workflow metadata from UnifiedViews
Scenario: Access to POST Workflows
       Given I access the url "/v0.1/workflow"
       Then I see get response 405
       Then I get response data 'Operation Not Allowed.'

 Scenario: Access to GET Workflows
        Given I access the url "/v0.1/workflow"
        Then I see get response 200 or 304
        When response is 200
        Then the Content-Type is text/turtle or application/ld+json
