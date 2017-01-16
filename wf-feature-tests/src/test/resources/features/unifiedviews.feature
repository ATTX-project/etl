Feature: UnifiedViews is running

    # API TEST - API for pipelines will return 200 even though there are no pipelines
    Scenario: UnifiedViews API add pipeline
        Given the UnifiedViews is running
        When we add a new pipeline
        Then we get the "workflow" from wfAPI.

    Scenario: UnifiedViews API run pipeline
        Given the UnifiedViews is running
        When we run a pipeline
        Then we get the "activity" from wfAPI.