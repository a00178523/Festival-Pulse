package com.ericsson.festivalpulse.karate;

import com.intuit.karate.junit5.Karate;

class CrowdAlertKarateTest {

    @Karate.Test
    Karate testCrowdAlert() {
        return Karate.run("classpath:karate/crowdalert/crowd-alert.feature");
    }
}
