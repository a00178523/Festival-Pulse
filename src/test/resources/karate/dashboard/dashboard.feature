Feature: Dashboard API

  Background:
    * url baseUrl
    # Create festival
    * path '/api/festivals'
    * request { name: 'Dashboard Test Fest', description: 'For dashboard tests' }
    * method POST
    * status 201
    * def festivalId = response.id
    # Create two areas
    * path '/api/festivals/' + festivalId + '/areas'
    * request { name: 'Main Stage', areaType: 'Stage' }
    * method POST
    * status 201
    * def areaId1 = response.id
    * path '/api/festivals/' + festivalId + '/areas'
    * request { name: 'Food Village', areaType: 'Food & Drink' }
    * method POST
    * status 201
    * def areaId2 = response.id

  Scenario: Dashboard returns correct total area count
    Given path '/api/festivals/' + festivalId + '/dashboard'
    When method GET
    Then status 200
    And match response.totalAreas == 2

  Scenario: Dashboard shows empty reports and alerts for fresh festival
    Given path '/api/festivals/' + festivalId + '/dashboard'
    When method GET
    Then status 200
    And match response.recentReports == '#[]'
    And match response.activeAlerts == '#[]'

  Scenario: Dashboard reflects submitted reports
    Given path '/api/festivals/' + festivalId + '/reports'
    And request { areaId: '#(areaId1)', crowdLevel: 'MEDIUM', note: 'Busy' }
    When method POST
    Then status 201

    Given path '/api/festivals/' + festivalId + '/dashboard'
    When method GET
    Then status 200
    And match response.recentReports == '#[1]'
    And match response.recentReports[0].crowdLevel == 'MEDIUM'

  Scenario: Dashboard reflects active alerts
    Given path '/api/festivals/' + festivalId + '/reports'
    And request { areaId: '#(areaId1)', crowdLevel: 'FULL', note: 'Packed' }
    When method POST
    Then status 201

    Given path '/api/festivals/' + festivalId + '/dashboard'
    When method GET
    Then status 200
    And match response.activeAlerts == '#[1]'
    And match response.activeAlerts[0].status == 'ACTIVE'

  Scenario: Dashboard active alerts decrease after resolve
    Given path '/api/festivals/' + festivalId + '/reports'
    And request { areaId: '#(areaId1)', crowdLevel: 'FULL' }
    When method POST
    Then status 201

    Given path '/api/festivals/' + festivalId + '/alerts'
    When method GET
    Then status 200
    * def alertId = response[0].id

    Given path '/api/festivals/' + festivalId + '/alerts/' + alertId + '/resolve'
    When method PATCH
    Then status 200

    Given path '/api/festivals/' + festivalId + '/dashboard'
    When method GET
    Then status 200
    And match response.activeAlerts == '#[]'

  Scenario: Dashboard returns 404 for non-existent festival
    Given path '/api/festivals/999999/dashboard'
    When method GET
    Then status 404
    And match response.message == 'Festival not found'
