Feature: Authentication
  As a user of the Elytra charging station system
  I want to be able to register and login
  So that I can access the system features based on my role

  Background:
    Given the system is initialized with clean database

  Scenario: Successful user registration as EV Driver
    When I register with the following details:
      | name     | John Doe              |
      | email    | john.doe@example.com  |
      | password | SecurePass123!        |
      | role     | EV_DRIVER             |
    Then the registration should be successful
    And the response should contain a JWT token
    And the user should be saved in the database with role "EV_DRIVER"

  Scenario: Successful user registration as Station Operator
    When I register with the following details:
      | name     | Jane Smith            |
      | email    | jane@station.com      |
      | password | StationPass456!       |
      | role     | STATION_OPERATOR      |
    Then the registration should be successful
    And the response should contain a JWT token
    And the user should be saved in the database with role "STATION_OPERATOR"

  Scenario: Registration fails with duplicate email
    Given a user exists with email "existing@example.com"
    When I register with the following details:
      | name     | New User              |
      | email    | existing@example.com  |
      | password | Password123!          |
      | role     | EV_DRIVER             |
    Then the registration should fail with status 400
    And the error message should contain "Email already registered"

  Scenario: Registration fails with invalid email format
    When I register with the following details:
      | name     | Invalid User          |
      | email    | invalid-email         |
      | password | Password123!          |
      | role     | EV_DRIVER             |
    Then the registration should fail with status 400
    And the error message should contain "Invalid email format"

  Scenario: Successful login with valid credentials
    Given a user exists with email "user@example.com" and password "Password123!"
    When I login with email "user@example.com" and password "Password123!"
    Then the login should be successful
    And the response should contain a JWT token
    And the response should contain the user details

  Scenario: Login fails with invalid credentials
    Given a user exists with email "user@example.com" and password "Password123!"
    When I login with email "user@example.com" and password "WrongPassword"
    Then the login should fail with status 401
    And the error message should contain "Invalid credentials"

  Scenario: Login fails with non-existent user
    When I login with email "nonexistent@example.com" and password "Password123!"
    Then the login should fail with status 401
    And the error message should contain "Invalid credentials"

  Scenario: Access protected endpoint with valid token
    Given I am logged in as an EV Driver
    When I access my profile endpoint
    Then the request should be successful
    And the response should contain my user details

  Scenario: Access protected endpoint without token
    When I access a protected endpoint without authentication
    Then the request should fail with status 401
    And the error message should contain "Authentication required"

  Scenario: Access admin endpoint as regular user
    Given I am logged in as an EV Driver
    When I access the admin users endpoint
    Then the request should fail with status 403
    And the error message should contain "Access denied"