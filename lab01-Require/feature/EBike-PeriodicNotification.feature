Feature: Periodic notification of ebike state
    In order to keep the service updated on current state
    As an ebike
    I want to periodically notify the service about my state

    Scenario: Periodic state update
        Given the ebike has a current state including location and battery level
        When the ebike periodically sends its current state to the system
        Then the system receives the data
        And the system updates the ebike's status for administrators

    Scenario: Unexpected data format
        Given the ebike sends data in an unexpected format
        When the system receives the message
        Then the system ignores message
