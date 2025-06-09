Feature: Admin Management
  As an administrator
  I want to manage the entire system
  So that I can ensure smooth operations and maintain system integrity

  Background:
    Given the system is initialized with clean database
    And I am logged in as an admin

  Scenario: View all users in the system
    Given the system has the following users:
      | name           | email                    | role              |
      | John Driver    | john@driver.com          | EV_DRIVER         |
      | Jane Operator  | jane@operator.com        | STATION_OPERATOR  |
      | Bob Driver     | bob@driver.com           | EV_DRIVER         |
      | Alice Admin    | alice@admin.com          | ADMIN             |
    When I request all users
    Then I should see 4 users
    And I can filter users by role

  Scenario: Deactivate a user account
    Given a user "problematic@user.com" exists
    When I deactivate the user account
    Then the user should be deactivated
    And the user should not be able to login

  Scenario: Reactivate a user account
    Given a deactivated user "inactive@user.com" exists
    When I reactivate the user account
    Then the user should be active again
    And the user should be able to login

  Scenario: View all stations in the system
    Given the system has stations from multiple operators
    When I request all stations
    Then I should see all stations regardless of operator
    And I should see station details including:
      | Station Name    |
      | Operator Name   |
      | Location        |
      | Number of Chargers |
      | Status          |

  Scenario: Override station status
    Given a station "Maintenance Station" exists
    When I change the station status to "UNDER_MAINTENANCE"
    Then all chargers at the station should be unavailable
    And users should see maintenance message

  Scenario: View system-wide statistics
    When I request system statistics
    Then I should see:
      | Total Users              |
      | Total Stations           |
      | Total Chargers           |
      | Active Bookings          |
      | Revenue This Month       |
      | Average Utilization Rate |

  Scenario: Generate usage report
    When I generate a usage report for the last 30 days
    Then I should receive a report containing:
      | Daily booking counts     |
      | Peak usage hours         |
      | Most popular stations    |
      | Revenue breakdown        |
      | User growth metrics      |

  Scenario: View all bookings with filters
    Given the system has bookings from multiple users
    When I request all bookings with filters:
      | dateFrom    | 2024-06-01 |
      | dateTo      | 2024-06-30 |
      | status      | COMPLETED  |
    Then I should see filtered bookings
    And I can export the results

  Scenario: Manage charger types
    When I add a new charger type "Ultra Fast CCS"
    Then the charger type should be available system-wide
    And station operators can use it for new chargers

  Scenario: Set global pricing rules
    When I set the following pricing rules:
      | chargerType | pricePerKwh | minimumCharge |
      | Type 2      | 0.25        | 2.00          |
      | CCS         | 0.35        | 3.00          |
      | CHAdeMO     | 0.35        | 3.00          |
    Then the pricing should apply to all stations
    And stations with custom pricing should keep their own rates

  Scenario: Emergency system shutdown
    Given there is a critical system issue
    When I initiate emergency shutdown
    Then all active charging sessions should be safely stopped
    And new bookings should be prevented
    And users should see maintenance message

  Scenario: View audit logs
    When I request audit logs for the last 7 days
    Then I should see all administrative actions including:
      | Action Type     |
      | Performed By    |
      | Timestamp       |
      | Affected Entity |
      | Changes Made    |

  Scenario: Bulk import stations
    When I upload a CSV file with station data:
      | name            | address        | city          | latitude | longitude |
      | Import Station1 | 123 Import St  | Import City   | 40.7128  | -74.0060  |
      | Import Station2 | 456 Import Ave | Import Town   | 41.8781  | -87.6298  |
    Then 2 stations should be created
    And import results should show success/failure for each row

  Scenario: Configure system notifications
    When I configure the following notifications:
      | event                  | enabled | recipients            |
      | CHARGER_ERROR         | true    | maintenance@elytra.com |
      | LOW_UTILIZATION       | true    | operations@elytra.com  |
      | PAYMENT_FAILED        | true    | finance@elytra.com     |
    Then notifications should be sent according to configuration
    And I should be able to view notification history