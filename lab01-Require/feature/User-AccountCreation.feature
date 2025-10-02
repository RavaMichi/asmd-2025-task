Feature: Creating a new user account
    In order to start using the service
    As a user without account
    I want to create a new user account

    Scenario: Create new account
        Given the user is not registered
        When the user requests to create a new account
        And the user provides a unique username and a password
        Then the system creates a new account with 0 credits
        And the system automatically logs the user in
        And the system sends to the user a session token related to the new account

    Scenario: Create new account with username already used
        Given the user is not registered
        When the user requests to create a new account
        And the user provides a username already used and a password
        Then the system cancels the operation and informs the user
