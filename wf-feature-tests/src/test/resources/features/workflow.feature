Feature: Handle retrieving of workflow metadata from UnifiedViews
    Scenario: Add new workflow description
        Given wfAPI is running
        When I access the URL workflow API and try to send something
        Then I should see get that operation is Not allowed.

    Scenario: Get workflow descriptions
        Given wfAPi is running
        When I access the URL workflow API and try to retrieve something
        Then I should see get a response
       
        
