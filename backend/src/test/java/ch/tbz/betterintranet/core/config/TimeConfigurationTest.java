package ch.tbz.betterintranet.core.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TimeConfigurationTest {

    @Autowired
    private Clock clock;

    @Test
    @DisplayName("the application has exactly one clock and it runs in the school's time zone")
    void clockUsesSchoolTimeZone() {
        assertThat(clock.getZone()).isEqualTo(ZoneId.of("Europe/Zurich"));
        assertThat(TimeConfiguration.SCHOOL_ZONE).isEqualTo(ZoneId.of("Europe/Zurich"));
    }
}
