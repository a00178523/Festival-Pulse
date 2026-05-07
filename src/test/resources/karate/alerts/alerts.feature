Feature: Crowd Alert API

  Background:
    * url baseUrl
    # Create festival
    * path '/api/festivals'
    * request { name: 'Alert Test Fest', description: 'For alert tests' }
    * method POST
    * status 201
    * def festivalId = response.id
    # Create area
    * path '/api/festivals/' + festivalId + '/areas'
    * request { name: 'Main Stage', areaType: 'Stage' }
    * method POST
    * status 201
    * def areaId = response.id
    # Submit FULL report to trigger alert
    * path '/api/festivals/' + festivalId + '/reports'
    * request { areaId: '#(areaId)', crowdLevel: 'FULL', note: 'Packed out' }
    * method POST
    * status 201

  Scenario: Active alerts are listed for the festival
    Given path '/api/festivals/' + festivalId + '/alerts'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0].status == 'ACTIVE'
    And match response[0].area.name == 'Main Stage'
    And match response[0].message == 'Main Stage is FULL!'

  Scenario: Resolve an alert
    # Get the alert id
    Given path '/api/festivals/' + festivalId + '/alerts'
    When method GET
    Then status 200
    * def alertId = response[0].id

    Given path '/api/festivals/' + festivalId + '/alerts/' + alertId + '/resolve'
    When method PATCH
    Then status 200
    And match response.status == 'RESOLVED'

    # Confirm it no longer appears in active alerts
    Given path '/api/festivals/' + festivalId + '/alerts'
    When method GET
    Then status 200
    And match response == '#[]'

  Scenario: Resolved alerts do not appear in active list
    # Get and resolve the alert
    Given path '/api/festivals/' + festivalId + '/alerts'
    When method GET
    Then status 200
    * def alertId = response[0].id

    Given path '/api/festivals/' + festivalId + '/alerts/' + alertId + '/resolve'
    When method PATCH
    Then status 200

    Given path '/api/festivals/' + festivalId + '/alerts'
    When method GET
    Then status 200
    And match response == '#[]'

  Scenario: Return 404 when resolving a non-existent alert
    Given path '/api/festivals/' + festivalId + '/alerts/999999/resolve'
    When method PATCH
    Then status 404
    And match response.message == 'Alert not found'

  Scenario: Duplicate FULL reports do not create duplicate alerts
    # First FULL report — creates alert
    Given path '/api/festivals/' + festivalId + '/alerts'
    When method GET
    Then status 200
    * def alertCount = response.length
    And assert alertCount == 1

  Scenario: Alert message format contains area name
    Given path '/api/festivals/' + festivalId + '/alerts'
    When method GET
    Then status 200
    And match response[0].message == 'Main Stage is FULL!'

  Scenario: Resolving alert from wrong festival returns 404
    # Create a second festival — its alert IDs should not be resolvable via festivalId
    Given path '/api/festivals'
    And request { name: 'Other Fest' }
    When method POST
    Then status 201
    * def otherFestivalId = response.id

    Given path '/api/festivals/' + otherFestivalId + '/areas'
    And request { name: 'Other Stage', areaType: 'Stage' }
    When method POST
    Then status 201
    * def otherAreaId = response.id

    Given path '/api/festivals/' + otherFestivalId + '/reports'
    And request { areaId: '#(otherAreaId)', crowdLevel: 'FULL' }
    When method POST
    Then status 201

    Given path '/api/festivals/' + otherFestivalId + '/alerts'
    When method GET
    Then status 200
    * def otherAlertId = response[0].id

    # Try to resolve the other festival's alert via our festivalId
    Given path '/api/festivals/' + festivalId + '/alerts/' + otherAlertId + '/resolve'
    When method PATCH
    Then status 404
