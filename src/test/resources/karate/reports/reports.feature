Feature: Crowd Report API

  Background:
    * url baseUrl
    # Create a festival
    * path '/api/festivals'
    * request { name: 'Report Test Fest', description: 'For report tests' }
    * method POST
    * status 201
    * def festivalId = response.id
    # Create an area
    * path '/api/festivals/' + festivalId + '/areas'
    * request { name: 'Food Village', areaType: 'Food & Drink' }
    * method POST
    * status 201
    * def areaId = response.id

  Scenario: Submit a valid crowd report
    Given path '/api/festivals/' + festivalId + '/reports'
    And request { areaId: '#(areaId)', crowdLevel: 'MEDIUM', note: 'Getting busy' }
    When method POST
    Then status 201
    And match response.crowdLevel == 'MEDIUM'
    And match response.note == 'Getting busy'
    And match response.area.name == 'Food Village'
    And match response.submittedAt == '#string'

  Scenario: List recent reports scoped to festival
    Given path '/api/festivals/' + festivalId + '/reports'
    And request { areaId: '#(areaId)', crowdLevel: 'LOW', note: 'Quiet' }
    When method POST
    Then status 201

    Given path '/api/festivals/' + festivalId + '/reports'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0].area.name == 'Food Village'

  Scenario: Reject report for a non-existent area
    Given path '/api/festivals/' + festivalId + '/reports'
    And request { areaId: 999999, crowdLevel: 'LOW' }
    When method POST
    Then status 404
    And match response.message == 'Area not found in this festival'

  Scenario: Reject report with missing required fields
    Given path '/api/festivals/' + festivalId + '/reports'
    And request { note: 'Missing area and level' }
    When method POST
    Then status 400
    And match response.errors.areaId == 'Area ID is required'
    And match response.errors.crowdLevel == 'Crowd level is required'

  Scenario: FULL report creates an active alert
    Given path '/api/festivals/' + festivalId + '/reports'
    And request { areaId: '#(areaId)', crowdLevel: 'FULL', note: 'Packed out' }
    When method POST
    Then status 201

    Given path '/api/festivals/' + festivalId + '/alerts'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0].area.name == 'Food Village'
    And match response[0].status == 'ACTIVE'
    And match response[0].message == 'Food Village is FULL!'

  Scenario: Reject report when area already has an active alert
    # Submit FULL to create alert
    Given path '/api/festivals/' + festivalId + '/reports'
    And request { areaId: '#(areaId)', crowdLevel: 'FULL', note: 'Packed' }
    When method POST
    Then status 201

    # Try to submit another report to the same area
    Given path '/api/festivals/' + festivalId + '/reports'
    And request { areaId: '#(areaId)', crowdLevel: 'MEDIUM', note: 'Still busy' }
    When method POST
    Then status 409

  Scenario: Submit LOW crowd report successfully
    Given path '/api/festivals/' + festivalId + '/reports'
    And request { areaId: '#(areaId)', crowdLevel: 'LOW' }
    When method POST
    Then status 201
    And match response.crowdLevel == 'LOW'

    # LOW report must NOT create an alert
    Given path '/api/festivals/' + festivalId + '/alerts'
    When method GET
    Then status 200
    And match response == '#[]'

  Scenario: Submit MEDIUM crowd report successfully
    Given path '/api/festivals/' + festivalId + '/reports'
    And request { areaId: '#(areaId)', crowdLevel: 'MEDIUM' }
    When method POST
    Then status 201
    And match response.crowdLevel == 'MEDIUM'

    # MEDIUM report must NOT create an alert
    Given path '/api/festivals/' + festivalId + '/alerts'
    When method GET
    Then status 200
    And match response == '#[]'

  Scenario: Report submitted at correct time
    Given path '/api/festivals/' + festivalId + '/reports'
    And request { areaId: '#(areaId)', crowdLevel: 'LOW', note: 'Quiet' }
    When method POST
    Then status 201
    And match response.submittedAt == '#string'
    And match response.submittedAt != null

  Scenario: Report for area in different festival is rejected
    # Create a second festival and area
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

    # Try to submit a report using otherAreaId against the original festivalId
    Given path '/api/festivals/' + festivalId + '/reports'
    And request { areaId: '#(otherAreaId)', crowdLevel: 'LOW' }
    When method POST
    Then status 404
    And match response.message == 'Area not found in this festival'
