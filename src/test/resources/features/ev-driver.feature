Feature: EV Driver Management
  As an EV driver
  I want to manage my profile and vehicles
  So that I can use the charging station services effectively

  Background:
    Given the system is initialized with clean database
    And I am logged in as an EV driver

  Scenario: View my profile
    When I request my profile information
    Then I should see my user details
    And I should see my EV driver information

  Scenario: Register a new car
    When I register a car with the following details:
      | brand         | Tesla          |
      | model         | Model 3        |
      | licensePlate  | ABC-1234       |
      | chargerType   | CCS            |
    Then the car should be registered successfully
    And the car should be linked to my profile

  Scenario: Register car with duplicate license plate
    Given another driver has a car with license plate "XYZ-5678"
    When I try to register a car with license plate "XYZ-5678"
    Then the registration should fail with status 400
    And the error message should contain "License plate already exists"

  Scenario: View my cars
    Given I have registered the following cars:
      | brand    | model    | licensePlate | chargerType |
      | Tesla    | Model 3  | TES-001     | CCS         |
      | Nissan   | Leaf     | NIS-002     | CHAdeMO     |
      | BMW      | i3       | BMW-003     | Type 2      |
    When I request my cars list
    Then I should see 3 cars
    And all cars should show complete details

  Scenario: Update car information
    Given I have a car with license plate "OLD-123"
    When I update the car with:
      | brand         | Tesla          |
      | model         | Model Y        |
      | chargerType   | CCS            |
    Then the car should be updated successfully
    And the license plate should remain "OLD-123"

  Scenario: Delete a car
    Given I have a car with license plate "DEL-456"
    When I delete the car
    Then the car should be deleted successfully
    And the car should not appear in my cars list

  Scenario: Cannot delete car with active bookings
    Given I have a car with license plate "BOOK-789"
    And the car has an active booking
    When I try to delete the car
    Then the deletion should fail with status 400
    And the error message should contain "Cannot delete car with active bookings"

  Scenario: View my booking history
    Given I have completed bookings in the past
    When I request my booking history
    Then I should see all my past bookings
    And the bookings should include:
      | Station Name   |
      | Charger ID     |
      | Date and Time  |
      | Duration       |
      | Total Cost     |
      | Status         |

  Scenario: View nearby stations
    Given my current location is latitude 37.7749 and longitude -122.4194
    When I search for nearby stations within 5 km
    Then I should see available stations
    And each station should show:
      | Name           |
      | Address        |
      | Distance       |
      | Available Chargers |
      | Charger Types  |

  Scenario: Filter stations by charger type
    Given there are stations with different charger types
    When I filter stations by charger type "CCS"
    Then I should only see stations with CCS chargers
    And stations without CCS chargers should not be shown

  Scenario: View real-time charger availability
    Given I select a station "Downtown Charging Hub"
    When I request real-time availability
    Then I should see the current status of each charger
    And the status should be one of:
      | AVAILABLE      |
      | CHARGING       |
      | ERROR          |
      | MAINTENANCE    |

  Scenario: Favorite a station
    Given I find a station "Favorite Station"
    When I add it to my favorites
    Then the station should be saved to my favorites
    And I should be able to quickly access it later

  Scenario: View favorite stations
    Given I have favorited 3 stations
    When I request my favorite stations
    Then I should see all 3 favorited stations
    And they should be ordered by most recently added