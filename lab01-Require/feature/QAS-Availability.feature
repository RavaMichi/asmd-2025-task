@quality @availability
Feature: Availability
  In order to minimize service downtime
  As the server hosting the ebike rental service
  I want the service to restart automatically after a crash in a reasonable time

  Background:
    Given the "EbikeRentalService" is deployed and running
    And health checks, monitoring and an automatic-restart policy are enabled for the service

  Scenario: Server crash and automatic restart within 60 seconds
    Given the service is reachable and the health endpoint "/health" returns OK
    When the server hosting the service fails unexpectedly
    Then the monitoring system detects the failure
    And the automatic-restart policy is triggered
    And the service becomes healthy again ("/health" returns OK)
    And the total measured downtime is less than "60" seconds