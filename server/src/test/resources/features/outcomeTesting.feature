Feature: Editing an Outcome with the PUT request

  Background:
    Given I have a database connection
    And I am the creator of an outcome with the title "OutcomeTitle" and unitName "OutcomeUnitName"
    And the database contains the following user information
      | firstName   | lastName  | primaryEmail      | passwordText  | dateOfBirth   | gender  |
      | Bob         | Larry     | larryb@gmail.com  | iamBob        | 12/13/1987    | Male    |

  @U33-ActivityOutcomes
  Scenario:  EditingAsExpected
    Given I am logged in as Bob with email "larryb@gmail.com" and password "iamBob"
    When I try to edit my outcome
    Then My outcome now has the title "EditedTitle" and unitName "EditedUnitName"