Feature: User Login

  As a registered OpenCart customer,
  I want to log into my account
  So that I can manage my orders and profile.

  @Login @Smoke
  Scenario Outline: Successful User Login with valid credentials
    Given I am on the OpenCart login page
    When I enter valid credentials with email "<Email>" and password "<Password>"
    And I click the Login button
    Then I should be successfully logged in

    Examples:
      | Email             | Password  |
      | testuser@demo.com | password  |
      | user2@example.com | Secure123 |


  @Login @Negative
  Scenario Outline: Failed User Login with invalid credentials
    Given I am on the OpenCart login page
    When I enter invalid credentials with email "<Email>" and password "<Password>"
    And I click the Login button
    Then I should see a login failure warning message

    Examples:
      | Email            | Password    |
      | wrong@demo.com   | badpassword |
      | testuser@demo.com | wrongpass  |
