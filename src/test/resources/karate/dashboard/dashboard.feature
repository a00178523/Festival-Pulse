Feature: Dashboard API

  Background:
    * url 'http://localhost:8081'

  Scenario: Dashboard returns summary with areas, reports and alerts
    Given path '/api/areas'
    And request { name: 'Main Stage', description: 'Primary area', areaType: 'Stage' }
    When method POST
    Then status 201
    * def areaId = response.id

    Given path '/api/reports'
    And request { areaId: '#(areaId)', crowdLevel: 'FULL' }
    When method POST
    Then status 201

    Given path '/api/dashboard'
    When method GET
    Then status 200
    And match response.totalAreas == '#number'
    And match response.recentReports == '#array'
    And match response.activeAlerts == '#array'
    And assert response.totalAreas >= 1
    And match response.activeAlerts[0].status == 'ACTIVE'

  Scenario: Dashboard returns empty summary when no data
    Given path '/api/dashboard'
    When method GET
    Then status 200
    And match response.totalAreas == '#number'
    And match response.recentReports == '#array'
    And match response.activeAlerts == '#array'
