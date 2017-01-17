Feature: Handle retrieving of workflow metadata from UnifiedViews

    Scenario: Try to add new workflow
        Given the wfAPI is running
        When I access the "workflow" API and try to send something
        Then I should see that operation is Not allowed.

    Scenario: Get workflow graph
        Given the wfAPI is running
        When I access the "workflow" API and try to retrieve something
        Then I should get a response with "workflows".
