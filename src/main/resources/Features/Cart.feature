@Cart
Feature: Shopping Cart Functionality

  Background:
    Given User is on Home page
    # Adding to cart requires a registered user -- perform registration first
    When I register with auto-generated details
    And I am on the OpenCart home page
    # Click the first featured product (no search) and add it to cart
    And User adds first product to cart

  Scenario: User can view shopping cart
    When User navigates to Cart page
    Then Cart page should be displayed
    And Cart header should be visible

  Scenario: User can add item to cart and verify
    When User searches for product "MacBook"
    And User adds first product to cart
    Then Product should be added to cart successfully
    And Cart should have items

  Scenario: User can update product quantity in cart
    When User navigates to Cart page
    And User has items in cart
    And User updates quantity to "2" for first item
    Then Cart should reflect updated quantity
    And Cart total should be updated

  Scenario: User can remove item from cart
    When User navigates to Cart page
    And User has items in cart
    And User removes first item from cart
    Then Item should be removed from cart
    And Cart should be updated

  Scenario: User can view cart summary and proceed to checkout
    When User navigates to Cart page
    And User has items in cart
    Then Cart summary should display subtotal
    And Cart summary should display total
    And User can proceed to checkout from cart

  Scenario: User can continue shopping from cart
    When User navigates to Cart page
    And User has items in cart
    And User clicks on Continue Shopping
    Then User should be redirected to Home page

  Scenario: Empty cart notification
    When User navigates to Cart page
    And Cart is empty
    Then Empty cart message should be displayed
    And Continue Shopping button should be available
