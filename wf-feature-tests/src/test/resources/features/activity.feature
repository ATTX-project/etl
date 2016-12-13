Feature: Handle retrieving of activity metadata from UnifiedViews

    Scenario: Add new workflow activity
        Given the wfAPI is running
        When I access the URL activity API and try to send something
        Then I should see that operation is Not allowed.

    Scenario: Get workflow activities
        Given the wfAPI is running
        When I access the URL activity API and try to retrieve something
        Then I should get a response with content
