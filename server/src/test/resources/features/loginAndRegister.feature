Feature: Logging In And Out of a User Profile

  Background:
    Given I have a database connection
    And the database contains the following user information
      | firstName   | lastName  | primaryEmail      | passwordText  | dateOfBirth   | gender  |
      | Bob         | Larry     | larryb@gmail.com  | iamBob        | 12/13/1987    | Male    |

  @U1-RegisteringAUserProfileAndLoggingIn
  Scenario:  LoggingInAndOutAsExpected
    Given I am logged in as Bob with email "larryb@gmail.com" and password "iamBob"
    When I log out
    Then my user (with email "larryb@gmail.com") has no token