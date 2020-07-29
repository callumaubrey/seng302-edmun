Feature: Recording my participation outcome in an activity

  Background:
    Given I create a user with email "poly@pocket.com" and password "Password1"
    And I log in with email "poly@pocket.com" and password "Password1"


  @U29
  #AC1
  Scenario: I can add the details of my participation in an activity with a duration metric
    Given there is an activity that has a duration metric
    And I am a participant of the activity
    When I add the details of my participation in the activity with duration of 30 seconds
    Then the details of my participation is recorded as 30 seconds for the duration metric

  @U29
  #AC1
  Scenario: I can add the details of my participation in an activity with a count metric
    Given there is an activity that has a count metric
    And I am a participant of the activity
    When I add the details of my participation in the activity with 50 count
    Then the details of my participation is recorded as 50 for the count metric

  @U29
  #AC1
  Scenario: I can add the details of my participation in an activity with a distance metric
    Given there is an activity that has a distance metric
    And I am a participant of the activity
    When I add the details of my participation in the activity with 2 km as a distance
    Then the details of my participation is recorded as 2 km for the distance metric

  @U29
  #AC1 and AC2
  Scenario: I can add the details of my participation in an activity with a start finish metric
    Given there is an activity that has a finish metric
    And I am a participant of the activity
    When I add the details of my participation in the activity
      | Start Time          | End Time            |
      | 2020-01-01 13:00:00 | 2020-01-02 13:00:00 |
    Then the details of my participation is recorded as
      | Start Time          | End Time            |
      | 2020-01-01 13:00:00 | 2020-01-02 13:00:00 |


  @U29
  #AC3
  Scenario: I can disqualify myself from the activity
    Given there is an activity
    And I am a participant of the activity
    When I add the details of my participation as disqualified
    Then the details of my participation is recorded disqualified


  @U29
  #AC3
  Scenario: I disqualify someone from my activity
    Given I am a creator of an activity
    And my activity has a participant
    When I disqualify the participant in the activity
    Then the details of the participant being disqualified is recorded in the activity


  @U29
  #AC3
  Scenario: I can record a technical failure for the activity that I participated in
    Given there is an activity
    And I am a participant of the activity
    When I add the details of my participation as technical failure
    Then the details of my participation is recorded technical failure


  @U29
  #AC3
  Scenario: I can record a did not finish outcome for the activity that I participated in
    Given there is an activity
    And I am a participant of the activity
    When I add the details of my participation as did not finish
    Then the details of my participation is recorded as did not finish

  @U29
  #AC4
  Scenario: I can view outcomes of activity that I participated in the home feed
    Given there is an activity that has a duration metric
    And I am a participant of the activity
    When I add the details of my participation in the activity with duration of 30 seconds
    Then the details of the outcome of my participation is recorded as 30 seconds will display in my home feed


  @U29
  #AC4
  Scenario: I can view outcomes of activity that I subscribed to in the home feed
    Given there is an activity that has a duration metric
    And I am a participant of the activity
    When A participant add the details of their participation in the activity with duration of 30 seconds
    Then the details of the outcome of their participation is recorded as 30 seconds will display in my home feed





