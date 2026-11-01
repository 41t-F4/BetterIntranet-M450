package ch.tbz.betterintranet.dashboard.infrastructure;

import java.time.Clock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Technical beans of the dashboard module. */
@Configuration
public class DashboardConfiguration {

    /** The wall clock used to decide which events are still upcoming. Tests replace it with a fixed clock. */
    @Bean
    @ConditionalOnMissingBean(Clock.class)
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
