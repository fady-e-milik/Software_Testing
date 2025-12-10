@Checkout
Feature: Checkout Process and Order Placement

  Background:
    Given User is on Home page
    And User has already logged in with valid credentials

  Scenario: User can proceed to checkout from cart
    When User searches for product "MacBook"
    And User adds first product to cart
    And User navigates to Cart page
    And User clicks Checkout button
    Then Checkout page should be displayed successfully
    And Billing address fields should be visible

  Scenario: User can complete checkout with billing information
    When User navigates to Checkout page
    And User fills billing address with firstName "John" lastName "Doe" email "john@example.com" address "123 Main St" city "London" postcode "E1 6AN"
    Then Billing address should be filled successfully
    And User can proceed to next step

  Scenario: User can select shipping method during checkout
    When User navigates to Checkout page
    And User fills billing address with firstName "Jane" lastName "Smith" email "jane@example.com" address "456 Oak Ave" city "London" postcode "E1 6AN"
    And User selects shipping method
    Then Shipping method should be selected successfully

  Scenario: User can select payment method during checkout
    When User navigates to Checkout page
    And User selects payment method
    Then Payment method should be selected successfully

  Scenario: User can place order successfully
    When User navigates to Checkout page
    And User has items in cart
    And User fills billing address with firstName "Test" lastName "User" email "test@example.com" address "789 Pine Rd" city "London" postcode "E1 6AN"
    And User selects shipping method
    And User selects payment method
    And User places order
    Then Order should be placed successfully
    And Order confirmation page should be displayed
    And Success message should appear

  Scenario: User can add comments to order during checkout
    When User navigates to Checkout page
    And User adds order comments "Please deliver after 5 PM"
    Then Comments should be saved
    And User can proceed to place order

  Scenario: User can view order total before placing order
    When User navigates to Checkout page
    And User has items in cart
    Then Order subtotal should be displayed
    And Order total should be displayed
    And Total should be calculated correctly
