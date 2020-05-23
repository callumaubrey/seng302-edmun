Feature: Create an admin account that can do any functionality available within the system

  Background:
    Given I log in as the default admin with email "test@test.com" and password "test"

  @U6
  Scenario: Admin can edit user's primary email
    Given I registered a test user
    When I edit the primary email of the user to "adminwashere@test.com"
    Then primary email of the user is "adminwashere@test.com"

  @U6
  Scenario: Admin can edit user's additional email
    When I add "adminwashere2@test.com" to the user's list of additional email
    Then "adminwashere2@test.com" is in the user's list of additional email

  @U6
  Scenario: Admin can edit user's password with old user password
    Given I change the user's password from "Cucumber123" to "Admin123"
    When I log out of the admin account
    Then I will log in successfully with the changed password "Admin123"

  @13
  Scenario: Admin can give another user admin rights
    When I give "adminwashere2@test.com" admin rights
    Then "adminwashere2@test.com" has user admin role

  @13
  Scenario: Admin can remove user admin rights
    Given "adminwashere2@test.com" has admin rights
    When I remove admin rights from "adminwashere2@test.com"
    Then "adminwashere2@test.com" is a normal user

  @13
  Scenario: Admin can edit user's password without old user password
    Given "adminwashere2@test.com" is a registered user
    When I change the password of "adminwashere2@test.com" to "Admin123"
    Then I can log into "adminwashere2@test.com" with password "Admin123"
