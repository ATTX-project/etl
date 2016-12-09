Feature: Handle retrieving of activity metadata from UnifiedViews
Scenario: Access to POST Activities
       Given access to the URL activity API I try to send something
       Then I see get that operation is Not allowed.

 Scenario: Access to GET Activities
        Given access to the URL activity API I try to retrieve something
        Then I see get a response
        When response I get a response
        I have content.
