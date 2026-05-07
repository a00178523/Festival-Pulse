Feature: Festival Area API

  Background:
    * url 'http://localhost:8081'

  Scenario: Create a new festival area
    Given path '/api/areas'
    And request { name: 'Main Stage', description: 'Primary performance area', areaType: 'Stage' }
    When method POST
    Then status 201
    And match response.id == '#number'
    And match response.name == 'Main Stage'
    And match response.description == 'Primary performance area'

  Scenario: Get all festival areas
    Given path '/api/areas'
    And request { name: 'Food Court', description: 'Dining area', areaType: 'Food' }
    When method POST
    Then status 201

    Given path '/api/areas'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0].name == '#string'
