@MyAccount
Feature: My Account Page Functionality

  Background:
    Given User is on Home page
    And User has already logged in with valid credentials

  Scenario: User can view My Account page
    When User navigates to My Account
    Then My Account page should be displayed successfully
    And User account details should be visible

  Scenario: User can update account information
    When User navigates to My Account
    And User clicks on Edit Account
    And User updates account information with firstName "John" and lastName "Doe"
    Then Account information should be updated successfully
    And Success message should be displayed

  Scenario: User can view order history
    When User navigates to My Account
    And User clicks on View Orders
    Then Order history section should be displayed
    And Previous orders should be visible

  Scenario: User can manage addresses
    When User navigates to My Account
    And User clicks on Address Book
    Then Address book page should be displayed
    And User can view saved addresses

  Scenario: User can view wish list
    When User navigates to My Account
    And User clicks on Wish List
    Then Wish list page should be displayed

  Scenario: User can change password
    When User navigates to My Account
    And User clicks on Change Password
    Then Password change form should be displayed
    And User can enter new password
