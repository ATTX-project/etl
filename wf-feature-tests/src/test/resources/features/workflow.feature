Feature: Handle retrieving of workflow metadata from UnifiedViews
Scenario: Access to POST Workflows
       Given access to the URL workflow API I try to send something
       Then I see get that operation is Not allowed.

 Scenario: Access to GET Workflows
        Given access to the URL workflow API I try to retrieve something
        Then I see get a response
        When response I get a response
        I have content.
