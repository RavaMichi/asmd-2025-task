Feature: User login
    In order to start using the service
    As a user
    I want to log into my account

    Scenario: Valid login
        Given a registered user exists with a username and password
        When the user requests to log into their account
        And the user provides a valid username and password
        Then the system authenticates the credentials
        And the system sends the user a session token and grants access to the account

    Scenario: Login with incorrect credentials
        Given a registered user exists
        When the user provides an incorrect username or password
        Then the system denies access and displays an error message
