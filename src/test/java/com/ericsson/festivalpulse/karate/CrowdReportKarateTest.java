package com.ericsson.festivalpulse.karate;

import com.intuit.karate.junit5.Karate;

class CrowdReportKarateTest {

    @Karate.Test
    Karate testCrowdReport() {
        return Karate.run("classpath:karate/crowdreport/crowd-report.feature");
    }
}
