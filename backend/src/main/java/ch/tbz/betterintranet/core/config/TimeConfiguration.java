package ch.tbz.betterintranet.core.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The one clock of the application. Every module that needs «today» injects this {@link Clock} instead of
 * calling {@code LocalDate.now()}, so the school's time zone is applied consistently and tests can replace
 * the clock with a fixed one.
 */
@Configuration
public class TimeConfiguration {

    /** Time zone of the TBZ; «today» and «this week» are always evaluated in it. */
    public static final ZoneId SCHOOL_ZONE = ZoneId.of("Europe/Zurich");

    @Bean
    Clock clock() {
        return Clock.system(SCHOOL_ZONE);
    }
}
