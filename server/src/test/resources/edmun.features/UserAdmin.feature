Feature: Create an user admin account that can do any functionality available within the system

  @13
  Scenario: Create a dummy user
    When I register a dummy user
    Then dummy user is created and I receive a 201 status code.

  @13
  Scenario: Log in as an admin user
    Given I create a user admin
    When I log in as user admin
    Then I am logged in as an admin user and I receive 200 status code

  @13
  Scenario: User admin can edit dummy user's primary email
    When I edit the primary email of the dummy user to "adminwashere@test.com"
    Then primary email of the dummy user is "adminwashere@test.com"

  @13
  Scenario: User admin can edit dummy user's additional email
    When I add "adminwashere2@test.com" to the dummy user's list of additional email
    Then "adminwashere2@test.com" is in the dummy user's list of additional email

  @13
  Scenario: User admin can edit dummy user's password
    When I change the dummy user's password from "Cucumber123" to "Admin123"
    And I log out of the user admin account
    Then I will log in successfully as the dummy user with the changed password "Admin123"

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
    Given I have a admin with email "test@test.com" and password "test"
    When I remove admin rights from "test@test.com"
    Then "test@test.com" still has admin rights
