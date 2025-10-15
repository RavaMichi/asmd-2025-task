Feature: Disconnecting from an Ebike
    In order to stop the rent and allow renting another ebike again
    As a user
    I want to disconnect from a ebike

    Scenario: Disconnect from currently connected ebike
        Given the user is currently connected to an ebike with ID "bike_id"
        When the user requests to disconnect from the currently connected ebike
        Then the system verifies the user's current connection to the ebike
        And the system computes the price for the rental and deducts it from the user credits
        And the system registers the disconnection and ends the rental session
        And the system makes the ebike available for other users to rent

    Scenario: Disconnect but user not connected to any ebike
        Given the user is not connected to this or any ebike
        When the user requests to disconnect from the currently connected ebike
        Then the system denies the operation and informs the user
