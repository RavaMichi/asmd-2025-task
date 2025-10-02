Feature: User credits recharge
    In order to continue using the service
    As a user
    I want to recharge my credit

    Scenario: Successful recharge
        Given the user is logged in
        When the user requests to recharge their account credit
        And the user provides a recharge <amount>
        Then the system processes the payment
        And the system adds the selected <amount> to the user's account balance

    Scenario: Invalid recharge amount
        Given the user is logged in
        When the user provides an invalid recharge <amount>
        Then the system denies the operation and displays an error message
        Examples: Invalid amounts
            | amount |
            | 0      |
            | -90    |
