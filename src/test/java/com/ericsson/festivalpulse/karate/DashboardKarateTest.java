package com.ericsson.festivalpulse.karate;

import com.intuit.karate.junit5.Karate;

class DashboardKarateTest {

    @Karate.Test
    Karate testDashboard() {
        return Karate.run("classpath:karate/dashboard/dashboard.feature");
    }
}
