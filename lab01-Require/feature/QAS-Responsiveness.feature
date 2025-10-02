@quality @responsiveness
Feature: Responsiveness
  In order to provide timely responses to users and devices
  As the server hosting the ebike rental service
  I want to reply to all incoming requests (with correct values or with a well-formed error) even in high-load conditions

  Background:
    Given the "EbikeRentalService" is deployed and running
    And the system can measure request response time precisely

  Scenario Outline: Single request under heavy load is handled in < 1s
    Given the server is under heavy load of <concurrent_requests> concurrent connections for <load_duration> seconds
    When a client sends a "<method>" request to "<endpoint>" with a valid payload
    Then the server replies (either with correct values or with a well-formed error)
    And the measured response time for that request is less than "1" second
    Examples:
      | concurrent_requests | load_duration | method | endpoint                      |
      | 200                 | 30s           | POST   | /api/ebikes/connect           |
      | 200                 | 30s           | POST   | /api/ebikes/disconnect        |
      | 200                 | 30s           | POST   | /api/auth/login               |
