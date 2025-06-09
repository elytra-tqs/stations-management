Feature: Station Management
  As a station operator
  I want to manage my charging stations
  So that EV drivers can find and use them

  Background:
    Given the system is initialized with clean database
    And I am logged in as a station operator

  Scenario: Create a new charging station
    When I create a station with the following details:
      | name         | Green Energy Station    |
      | address      | 123 Main St            |
      | city         | San Francisco          |
      | latitude     | 37.7749                |
      | longitude    | -122.4194              |
      | isPublic     | true                   |
    Then the station should be created successfully
    And the station should be linked to my operator account
    And the station should appear in the station list

  Scenario: Create station with invalid coordinates
    When I create a station with the following details:
      | name         | Invalid Station        |
      | address      | 456 Invalid St         |
      | city         | Invalid City           |
      | latitude     | 200.0                  |
      | longitude    | -300.0                 |
      | isPublic     | true                   |
    Then the station creation should fail with status 400
    And the error message should contain "Invalid coordinates"

  Scenario: Update existing station details
    Given I have a station named "My Station"
    When I update the station with:
      | name         | Updated Station Name   |
      | address      | 789 New Address        |
      | city         | New City               |
    Then the station should be updated successfully
    And the station details should reflect the changes

  Scenario: Delete a station
    Given I have a station named "Station to Delete"
    When I delete the station
    Then the station should be deleted successfully
    And the station should not appear in the station list

  Scenario: Add charger to station
    Given I have a station named "Charging Hub"
    When I add a charger with the following details:
      | identifier   | CHG-001                |
      | power        | 150.0                  |
      | type         | CCS                    |
      | status       | AVAILABLE              |
    Then the charger should be added successfully
    And the station should have 1 charger

  Scenario: Add multiple chargers to station
    Given I have a station named "Multi-Charger Station"
    When I add the following chargers:
      | identifier | power | type      | status    |
      | CHG-001   | 50.0  | Type 2    | AVAILABLE |
      | CHG-002   | 150.0 | CCS       | AVAILABLE |
      | CHG-003   | 350.0 | CCS       | AVAILABLE |
    Then all chargers should be added successfully
    And the station should have 3 chargers

  Scenario: Update charger status
    Given I have a station with a charger "CHG-001"
    When I update the charger status to "MAINTENANCE"
    Then the charger status should be updated successfully
    And the charger should show status "MAINTENANCE"

  Scenario: List stations by operator
    Given I have the following stations:
      | name              | city          | isPublic |
      | Station Alpha     | New York      | true     |
      | Station Beta      | Los Angeles   | true     |
      | Station Gamma     | Chicago       | false    |
    When I request my stations list
    Then I should see 3 stations
    And all stations should belong to me

  Scenario: Search public stations by location
    Given the following stations exist:
      | name              | latitude  | longitude   | isPublic |
      | Near Station      | 37.7749   | -122.4194   | true     |
      | Far Station       | 40.7128   | -74.0060    | true     |
      | Private Station   | 37.7740   | -122.4180   | false    |
    When I search for stations near latitude 37.7749 and longitude -122.4194 within 10 km
    Then I should see 1 station
    And the station should be "Near Station"

  Scenario: View station statistics
    Given I have a station "Popular Station" with completed bookings
    When I request statistics for the station
    Then I should see:
      | Total Bookings     | 25                    |
      | Revenue            | 450.00                |
      | Average Duration   | 45 minutes            |
      | Utilization Rate   | 65%                   |

  Scenario: Operator cannot modify other operator's station
    Given another operator has a station named "Other Station"
    When I try to update "Other Station"
    Then the request should fail with status 403
    And the error message should contain "Access denied"