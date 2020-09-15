Feature: Editing an Outcome with the PUT request

  Background:
    Given I am the creator of a basic activity
    And My activity has an outcome with the title "OutcomeTitle" and unitName "OutcomeUnitName"

  @U33-ActivityOutcomes
  Scenario:  EditingAsExpected
    When I try to edit my outcome
    Then My outcome now has the title "EditedTitle" and unitName "EditedUnitName"