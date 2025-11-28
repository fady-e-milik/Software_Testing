Feature: User Registration
  As a new user
  I want to register an account on OpenCart

  @Registration
  Scenario Outline: Successful User Registration with Various Data
    Given I am on the OpenCart registration page
    When I enter "<FirstName>", "<LastName>", "<Email>" and "<Password>"
    And I agree to the privacy policy
    And I click the Continue button
    Then my account should be successfully created

    Examples:
      | FirstName | LastName | Email              | Password  |
      | John      | Doe      | john.d@test.com    | Secure123 |
      | Jane      | Smith    | jane.s@test.com    | Strong456 |
      | Test      | User     | test.user@test.com | Pass789   |