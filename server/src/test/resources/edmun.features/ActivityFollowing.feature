Feature: Following an activity

  Background:
    Given I log in as a user with email "poly@pocket.com" and password "Password1"

  @U16
  #AC1
  Scenario: Follow a public activity
    Given There is an activity "My cool activity"
    When I follow the activity "My cool activity"
    Then User with email "poly@pocket.com" is a follower of "My cool activity"

  @U16
  #AC2
  Scenario: Unfollowing a public activity
    Given There is an activity "My cool activity"
    And I follow the activity "My cool activity"
    When I unfollow the activity "My cool activity"
    Then User with email "poly@pocket.com" is not a follower of "My cool activity"

  @U16
  #AC3
  Scenario: Following adds to home feed
    Given There is an activity "My cool activity"
    When I follow the activity "My cool activity"
    Then User with email "poly@pocket.com" home feed shows
      | Activity Name |
      | My cool activity |

  @U16
  #AC3
  Scenario: Unfollows adds to home feed
    Given There is an activity "My cool activity"
    And I follow the activity "My cool activity"
    When I unfollow the activity "My cool activity"
    Then User with email "poly@pocket.com" home feed shows
      | Activity Name |
      | My cool activity |
      | My cool activity |

  @U16
  #AC4
  Scenario: Home feed changes
    Given There is an activity "My cool activity"
    And I follow the activity "My cool activity"
    When the activity "My cool activity" has its description changed to "My coolest activity"
    Then User with email "poly@pocket.com" home feed shows
      | Activity Name |
      | My cool activity |
      | My cool activity |