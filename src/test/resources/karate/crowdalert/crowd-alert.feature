Feature: Crowd Alert API

  Background:
    * url 'http://localhost:8081'

  Scenario: Get active alerts and resolve an alert
    Given path '/api/areas'
    And request { name: 'Food Court', description: 'Dining area', areaType: 'Food' }
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
    And match response == '#array'
    And match response[0].status == 'ACTIVE'
    * def alertId = response[0].id

    Given path '/api/alerts/' + alertId + '/resolve'
    When method PATCH
    Then status 200
    And match response.status == 'RESOLVED'
