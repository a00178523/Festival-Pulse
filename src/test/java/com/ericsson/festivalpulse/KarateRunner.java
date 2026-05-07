package com.ericsson.festivalpulse;

import com.intuit.karate.junit5.Karate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class KarateRunner {

    @Karate.Test
    Karate runAll() {
        return Karate.run("classpath:karate").relativeTo(getClass());
    }
}
