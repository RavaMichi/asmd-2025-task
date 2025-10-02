Feature: Monitoring the state of the system
    In order to run analysis on the system's state
    As an administrator
    I want to monitor the state of the system

    Scenario: View system state
        Given the administrator is authenticated
        When the administrator requests to see the system's state
        Then the system collects data from all ebikes and user activity logs and sends them to the administrator
        And the administrator reviews the state and conducts analysis
