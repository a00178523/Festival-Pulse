Feature: Festival Area API

  Background:
    * url baseUrl
    # Create a festival to use in all scenarios
    * path '/api/festivals'
    * request { name: 'Area Test Fest', description: 'For area tests' }
    * method POST
    * status 201
    * def festivalId = response.id

  Scenario: Create an area and list it
    Given path '/api/festivals/' + festivalId + '/areas'
    And request { name: 'Main Stage', description: 'Main outdoor stage', areaType: 'Stage' }
    When method POST
    Then status 201
    And match response.name == 'Main Stage'
    And match response.areaType == 'Stage'
    And match response.id == '#number'

    Given path '/api/festivals/' + festivalId + '/areas'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[*].name contains 'Main Stage'

  Scenario: Reject area creation with missing name
    Given path '/api/festivals/' + festivalId + '/areas'
    And request { areaType: 'Stage' }
    When method POST
    Then status 400
    And match response.errors.name == 'Name is required'

  Scenario: Return 404 for areas of a non-existent festival
    Given path '/api/festivals/999999/areas'
    When method GET
    Then status 404
    And match response.message == 'Festival not found'

  Scenario: Reject area creation with empty body
    Given path '/api/festivals/' + festivalId + '/areas'
    And request {}
    When method POST
    Then status 400
    And match response.errors.name == 'Name is required'

  Scenario: Area created with only required name field
    Given path '/api/festivals/' + festivalId + '/areas'
    And request { name: 'Minimal Area' }
    When method POST
    Then status 201
    And match response.name == 'Minimal Area'
    And match response.id == '#number'

  Scenario: Multiple areas can be created for the same festival
    Given path '/api/festivals/' + festivalId + '/areas'
    And request { name: 'Stage A', areaType: 'Stage' }
    When method POST
    Then status 201

    Given path '/api/festivals/' + festivalId + '/areas'
    And request { name: 'Stage B', areaType: 'Stage' }
    When method POST
    Then status 201

    Given path '/api/festivals/' + festivalId + '/areas'
    When method GET
    Then status 200
    And match response == '#array'
    And assert response.length >= 2
