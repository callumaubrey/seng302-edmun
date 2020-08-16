Feature: Adding performance metrics to an activity for users to record their results into

  Background:
    Given I registered a user with email "poly@pocket.com" and password "Password1"
    And I log in as a user with email "poly@pocket.com" and password "Password1"

    # AC1 & AC2 Create an activity with metrics
  @U33
  Scenario: Create an activity with metrics
    Given there are no activities in the database
    When I create an activity "I hate cucumber tests" with metrics
      | Name                | Unit            |
      | Checkpoints Reached | Count           |
      | Distance Travelled  | Distance        |
      | Start and Stop Time | TimeStartFinish |
      | Time waited         | TimeDuration    |
    Then I will receive "201" status code
    And there will be an activity "I hate cucumber tests" with metrics
      | Name                | Unit            |
      | Checkpoints Reached | Count           |
      | Distance Travelled  | Distance        |
      | Start and Stop Time | TimeStartFinish |
      | Time waited         | TimeDuration    |

      # AC2 Create activity without metrics
  @U33
  Scenario: Create an activity without metrics
    Given there are no activities in the database
    When I create an activity "I hate cucumber tests" without metrics
    Then I will receive "201" status code
    And there will be an activity "I hate cucumber tests" without metrics

      # AC1 Edit an activity
  @U33
  Scenario: Edit an activity and add metrics
    Given there are no activities in the database
    And I create an activity "I hate cucumber tests"
    When I add metrics to activity "I hate cumcumber tests"
      | Name               | Unit     |
      | Eggs Grabbed       | Count    |
      | Distance Travelled | Distance |
    Then there will be an activity "I hate cucumber tests" with metrics
      | Name               | Unit     |
      | Eggs Grabbed       | Count    |
      | Distance Travelled | Distance |

      # AC1 delete a metric
  @U33
  Scenario: Edit an activity and remove metrics
    Given there are no activities in the database
    And I create an activity "I hate cucumber tests" with metrics
      | Name               | Unit     |
      | Eggs Grabbed       | Count    |
      | Distance Travelled | Distance |
    When I delete metrics from activity "I hate cucumber tests"
      | Name         | Unit  |
      | Eggs Grabbed | Count |
    Then there will be an activity "I hate cucumber tests" with metrics
      | Name               | Unit     |
      | Distance Travelled | Distance |