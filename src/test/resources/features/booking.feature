Feature: Booking Management
  As an EV driver
  I want to book charging slots
  So that I can ensure a charger is available when I need it

  Background:
    Given the system is initialized with clean database
    And the following stations exist:
      | name              | city          | latitude  | longitude   |
      | Downtown Station  | San Francisco | 37.7749   | -122.4194   |
      | Airport Station   | San Francisco | 37.6213   | -122.3789   |
    And "Downtown Station" has the following chargers:
      | identifier | power | type   | status    |
      | CHG-001   | 50.0  | Type 2 | AVAILABLE |
      | CHG-002   | 150.0 | CCS    | AVAILABLE |

  Scenario: Create a booking for available time slot
    Given I am logged in as an EV driver
    And I have a car with charger type "CCS"
    When I create a booking with:
      | station    | Downtown Station           |
      | charger    | CHG-002                   |
      | startTime  | 2024-06-10T10:00:00       |
      | endTime    | 2024-06-10T11:00:00       |
    Then the booking should be created successfully
    And the booking status should be "CONFIRMED"
    And I should receive a booking confirmation

  Scenario: Cannot book overlapping time slot
    Given I am logged in as an EV driver
    And there is an existing booking for "CHG-002" from "2024-06-10T10:00:00" to "2024-06-10T11:00:00"
    When I create a booking with:
      | station    | Downtown Station           |
      | charger    | CHG-002                   |
      | startTime  | 2024-06-10T10:30:00       |
      | endTime    | 2024-06-10T11:30:00       |
    Then the booking should fail with status 409
    And the error message should contain "Time slot not available"

  Scenario: List available time slots
    Given I am logged in as an EV driver
    And there are existing bookings for "Downtown Station"
    When I request available time slots for date "2024-06-10"
    Then I should see available time slots
    And the slots should not overlap with existing bookings

  Scenario: Cancel own booking
    Given I am logged in as an EV driver
    And I have a future booking for "Downtown Station"
    When I cancel the booking
    Then the booking should be cancelled successfully
    And the booking status should be "CANCELLED"
    And the time slot should become available

  Scenario: Cannot cancel past booking
    Given I am logged in as an EV driver
    And I have a past booking for "Downtown Station"
    When I try to cancel the booking
    Then the cancellation should fail with status 400
    And the error message should contain "Cannot cancel past booking"

  Scenario: View my bookings
    Given I am logged in as an EV driver
    And I have the following bookings:
      | station          | charger | date       | status     |
      | Downtown Station | CHG-001 | 2024-06-10 | CONFIRMED  |
      | Airport Station  | CHG-001 | 2024-06-11 | CONFIRMED  |
      | Downtown Station | CHG-002 | 2024-06-09 | COMPLETED  |
    When I request my bookings list
    Then I should see 3 bookings
    And the bookings should be sorted by date descending

  Scenario: Start charging session from booking
    Given I am logged in as an EV driver
    And I have a current booking for "CHG-001" at "Downtown Station"
    When I start the charging session
    Then the session should start successfully
    And the charger status should change to "CHARGING"
    And the booking status should be "IN_PROGRESS"

  Scenario: Complete charging session
    Given I am logged in as an EV driver
    And I have an active charging session
    When I stop the charging session
    Then the session should end successfully
    And the charger status should change to "AVAILABLE"
    And the booking status should be "COMPLETED"
    And I should see the session summary with:
      | Duration     | 45 minutes    |
      | Energy Used  | 22.5 kWh      |
      | Total Cost   | $11.25        |

  Scenario: Book with incompatible car charger type
    Given I am logged in as an EV driver
    And I have a car with charger type "Type 1"
    When I try to book charger "CHG-002" which is type "CCS"
    Then the booking should fail with status 400
    And the error message should contain "Incompatible charger type"

  Scenario: Station operator views station bookings
    Given I am logged in as a station operator
    And I own "Downtown Station"
    When I request bookings for my station
    Then I should see all bookings for the station
    And I should see booking details including:
      | Driver Name  |
      | Car Details  |
      | Time Slot    |
      | Status       |

  Scenario: Admin views all system bookings
    Given I am logged in as an admin
    When I request all system bookings
    Then I should see bookings from all stations
    And I can filter by:
      | Station      |
      | Date Range   |
      | Status       |
      | Driver       |