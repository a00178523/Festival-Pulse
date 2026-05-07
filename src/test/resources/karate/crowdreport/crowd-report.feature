Feature: Crowd Report API

  Background:
    * url 'http://localhost:8081'
    * def areaPayload = { name: 'Main Stage', description: 'Primary area', areaType: 'Stage' }

  Scenario: Submit a valid crowd report
    Given path '/api/areas'
    And request areaPayload
    When method POST
    Then status 201
    * def areaId = response.id

    Given path '/api/reports'
    And request { areaId: '#(areaId)', crowdLevel: 'MEDIUM', note: 'Getting crowded' }
    When method POST
    Then status 201
    And match response.crowdLevel == 'MEDIUM'
    And match response.note == 'Getting crowded'

  Scenario: Reject report for missing area
    Given path '/api/reports'
    And request { areaId: 99999, crowdLevel: 'LOW', note: 'Test' }
    When method POST
    Then status 404

  Scenario: FULL report creates an alert
    Given path '/api/areas'
    And request areaPayload
    When method POST
    Then status 201
    * def areaId = response.id

    Given path '/api/reports'
    And request { areaId: '#(areaId)', crowdLevel: 'FULL' }
    When method POST
    Then status 201

    Given path '/api/alerts'
    When method GET
    Then status 200
    And match response[*].status contains 'ACTIVE'
