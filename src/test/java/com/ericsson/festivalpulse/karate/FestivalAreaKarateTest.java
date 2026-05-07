package com.ericsson.festivalpulse.karate;

import com.intuit.karate.junit5.Karate;

class FestivalAreaKarateTest {

    @Karate.Test
    Karate testFestivalArea() {
        return Karate.run("classpath:karate/festivalarea/festival-area.feature");
    }
}
