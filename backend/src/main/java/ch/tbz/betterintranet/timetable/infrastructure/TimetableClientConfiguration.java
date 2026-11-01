package ch.tbz.betterintranet.timetable.infrastructure;

import ch.tbz.betterintranet.core.config.BetterIntranetProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/** Builds the module-private {@link RestClient} pointing at {@code betterintranet.timetable.base-url}. */
@Configuration
public class TimetableClientConfiguration {

    @Bean
    RestClient timetableRestClient(RestClient.Builder builder, BetterIntranetProperties properties) {
        throw new UnsupportedOperationException("not implemented");
    }
}
