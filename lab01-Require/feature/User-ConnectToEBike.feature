Feature: Connecting to an Ebike
    In order to rent that ebike
    As a user
    I want to connect to a ebike

    Scenario: Connect to ebike
        Given the user is logged in
        When the user requests to connect to an ebike
        And the user provides ID "bike_id"
        Then the system verifies the user has enough credits
        And the system verifies the ebike "bike_id" is not connected to another user
        And the system registers the connection between the user and the ebike "bike_id"
        And the user starts using the ebike

    Scenario: Connect to ebike with wrong ID
        Given the user is logged in
        When the user requests to connect to an ebike
        And the user provides a wrong ebike ID "bike_id"
        Then the system denies the connection and displays an error message

    Scenario: Connect to ebike with not enough credits
        Given the user is logged in with insufficient credits
        When the user requests to connect to an ebike
        And the user provides ID "bike_id"
        Then the system denies the connection and informs the user

    Scenario: Connect to ebike already in use
        Given the user is logged in
        And the ebike with ID "bike_id" is already used by another user
        When the user requests to connect to an ebike
        And the user provides ID "bike_id"
        Then the system denies the connection and informs the user
