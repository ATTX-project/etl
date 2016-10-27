#----------------------------------
# Login feature
#----------------------------------
    
Feature: Users can login and have rights permissions assigned to them
             

    # Simple login
    Scenario: User login
        Given the user "X" has an account
        When he logs in
        Then he should see "welcome X"

    Scenario: Admin login
        Given user "X" has acount
        And he is admin
        When he logs in
        Then he should see "welcome X"
        And he should see "admin" link