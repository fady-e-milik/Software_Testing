Feature: Home Page Functionality and Navigation

  As a visitor or customer,
  I want to interact with the home page search and navigation links
  So that I can find products and access my account.

  @Home @Search
  Scenario Outline: Successful product search from the home page
    Given I am on the OpenCart home page
    When I search for product "<ProductName>"
    Then I should be navigated to the search results page for "<ProductName>"

    Examples:
      | ProductName |
      | iPhone      |
      | Samsung     |
      | Mouse       |


  @Home @Navigation
  Scenario: Navigate to Login Page
    Given I am on the OpenCart home page
    When I click the Login link in the header
    Then I should be successfully navigated to the Login Page

  @Home @Navigation
  Scenario: Navigate to Registration Page
    Given I am on the OpenCart home page
    When I click the Register link in the header
    Then I should be successfully navigated to the Registration Page