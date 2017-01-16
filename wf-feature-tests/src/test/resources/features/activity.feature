Feature: Handle retrieving of activity metadata from UnifiedViews

    Scenario: Add new activity activity
        Given the wfAPI is running
        When I access the "activity" API and try to send something
        Then I should see that operation is Not allowed.

    Scenario: Get activity activities
        Given the wfAPI is running
        When I access the "activity" API and try to retrieve something
        Then I should get a response with "activities".
