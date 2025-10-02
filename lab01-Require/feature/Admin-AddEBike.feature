Feature: Adding an ebike
    In order to make that ebike available to the users
    As an administrator
    I want to add an ebike

    Scenario: Add new ebike
        Given the administrator is authenticated
        When the administrator requests to add a new ebike
        And the administrator provides the required details for the new ebike ("bike_id", "model", "battery_capacity")
        Then the system makes the ebike available for users to connect to and rent

    Scenario: Add already present ebike
        Given the administrator is authenticated
        When the administrator provides an ebike "bike_id" already present in the system
        Then the system denies the operation and displays an error message
