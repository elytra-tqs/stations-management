Feature: Station Search and Discovery
  As a user of the system
  I want to search and discover charging stations
  So that I can find the most suitable station for my needs

  Background:
    Given the system is initialized with clean database
    And the following stations exist with chargers:
      | name                  | city          | latitude  | longitude   | isPublic | chargerTypes      |
      | Downtown Fast Charge  | San Francisco | 37.7749   | -122.4194   | true     | CCS,CHAdeMO       |
      | Airport Station       | San Francisco | 37.6213   | -122.3789   | true     | Type 2,CCS        |
      | Shopping Mall Charge  | Oakland       | 37.8044   | -122.2712   | true     | Type 1,Type 2     |
      | Private Office Park   | San Jose      | 37.3382   | -121.8863   | false    | Type 2            |
      | Highway Rest Stop     | Sacramento    | 38.5816   | -121.4944   | true     | CCS,Tesla         |

  Scenario: Search stations by city
    When I search for stations in city "San Francisco"
    Then I should see 2 stations
    And the results should include "Downtown Fast Charge" and "Airport Station"

  Scenario: Search stations by proximity
    Given my location is latitude 37.7749 and longitude -122.4194
    When I search for stations within 10 km radius
    Then I should see stations ordered by distance
    And "Downtown Fast Charge" should be the closest

  Scenario: Search with expanded radius
    Given my location is latitude 37.7749 and longitude -122.4194
    When I search for stations within 50 km radius
    Then I should see more stations than 10 km search
    And results should include stations from Oakland

  Scenario: Filter search by charger type
    When I search for stations with charger type "CCS"
    Then I should see 3 stations
    And all results should have CCS chargers

  Scenario: Filter search by multiple charger types
    When I search for stations with charger types "Type 2" OR "CCS"
    Then I should see 4 stations
    And each result should have at least one of the requested types

  Scenario: Search public stations only
    When I search for public stations only
    Then I should see 4 stations
    And "Private Office Park" should not be in results

  Scenario: Combined search filters
    Given my location is latitude 37.7749 and longitude -122.4194
    When I search with the following criteria:
      | radius       | 30 km     |
      | chargerType  | CCS       |
      | isPublic     | true      |
    Then I should see stations matching all criteria
    And results should be ordered by distance

  Scenario: Search with availability filter
    Given "Downtown Fast Charge" has 1 available and 1 occupied charger
    When I search for stations with available chargers
    Then "Downtown Fast Charge" should appear in results
    And the result should show "1 of 2 chargers available"

  Scenario: Search stations by name
    When I search for stations with name containing "Airport"
    Then I should see 1 station
    And the result should be "Airport Station"

  Scenario: Fuzzy search for station names
    When I search for stations with name "Dwontown Charge"
    Then I should see "Downtown Fast Charge" in results
    And the search should handle typos

  Scenario: Search with no results
    When I search for stations in city "Alaska"
    Then I should see 0 stations
    And I should see a message "No stations found matching your criteria"

  Scenario: Search suggestions
    When I start typing "San" in the city search
    Then I should see suggestions:
      | San Francisco |
      | San Jose      |
    And selecting a suggestion should update the search

  Scenario: Save search criteria
    Given I am logged in as an EV driver
    When I search with specific criteria and save it as "My Work Commute"
    Then the search should be saved to my profile
    And I can quickly run the same search later

  Scenario: Search history
    Given I am logged in as an EV driver
    And I have performed several searches
    When I view my search history
    Then I should see my recent searches
    And I can re-run any previous search

  Scenario: Advanced search with operating hours
    When I search for stations open at "22:00" on "Sunday"
    Then I should only see 24/7 stations
    And stations with restricted hours should be excluded

  Scenario: Search with real-time pricing
    When I search for stations with price less than "$0.30 per kWh"
    Then I should see stations within the price range
    And each result should display current pricing

  Scenario: Map-based search
    When I draw a polygon on the map covering Oakland area
    Then I should see stations within the drawn area
    And "Shopping Mall Charge" should be included

  Scenario: Search along route
    Given I plan a route from San Francisco to Sacramento
    When I search for stations along the route with 5 km deviation
    Then I should see stations near the route
    And "Highway Rest Stop" should be suggested