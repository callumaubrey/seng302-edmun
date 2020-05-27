Feature: Create an user admin account that can do any functionality available within the system

  @13
  Background:
    Given There is a normal user with email "adminwashere2@test.com" registered in the database
    And There is a user admin with email "useradmin@test.com" registered in the database
    And I log in as a user admin with email "useradmin@test.com"
    And I receive response status code 200

  @13
  Scenario: User admin can edit another user's primary email
    When I edit the primary email of another user to "adminwashere@test.com"
    Then primary email of another user is "adminwashere@test.com"

  @13
  Scenario: User admin can edit another user's additional email
    When I add "adminwashere3@test.com" to another user's list of additional email
    Then "adminwashere3@test.com" is in another user's list of additional email

  @13
  Scenario: User admin can edit another user's password
    When I change another user's password to "Admin123" with old password
    And I log out of the user admin account
    Then I can log in as the other user with the changed password "Admin123"
    And I receive response status code 200

  @13
  Scenario: User admin can give another user admin rights
    When I give "adminwashere2@test.com" admin rights
    Then "adminwashere2@test.com" has user admin role

  @13
  Scenario: User admin can remove user admin rights
    Given "adminwashere2@test.com" has admin rights
    When I remove admin rights from "adminwashere2@test.com"
    Then "adminwashere2@test.com" is a normal user

  @13
  Scenario: User admin can edit user's password without old user password
    When I change the password of "adminwashere2@test.com" to "Admin123"
    Then I can log into "adminwashere2@test.com" with password "Admin123"

  @13
  Scenario: User admin cannot remove admin role from default admin
    Given I have an admin with email "test@test.com" and password "test"
    When I remove admin rights from "test@test.com"
    Then "test@test.com" still has admin rights
