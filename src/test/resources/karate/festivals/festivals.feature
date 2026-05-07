Feature: Festival API

  Background:
    * url baseUrl

  Scenario: Create a festival and retrieve it
    Given path '/api/festivals'
    And request { name: 'Test Fest', description: 'A test festival' }
    When method POST
    Then status 201
    And match response.name == 'Test Fest'
    And match response.id == '#number'
    * def festivalId = response.id

    Given path '/api/festivals'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[*].name contains 'Test Fest'

  Scenario: Reject festival creation with missing name
    Given path '/api/festivals'
    And request { description: 'No name provided' }
    When method POST
    Then status 400
    And match response.errors.name == 'Name is required'

  Scenario: Reject festival creation with empty body
    Given path '/api/festivals'
    And request {}
    When method POST
    Then status 400
    And match response.errors.name == 'Name is required'

  Scenario: Reject festival creation with blank name
    Given path '/api/festivals'
    And request { name: '   ' }
    When method POST
    Then status 400
    And match response.errors.name == 'Name is required'

  Scenario: Festival with optional fields omitted is created successfully
    Given path '/api/festivals'
    And request { name: 'Minimal Fest' }
    When method POST
    Then status 201
    And match response.name == 'Minimal Fest'
    And match response.id == '#number'
