package ch.tbz.betterintranet.absences.infrastructure;

import ch.tbz.betterintranet.core.config.BetterIntranetProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/** Module-private HTTP client for the school system ({@code betterintranet.school.base-url}). */
@Configuration
class AbsencesClientConfiguration {

    @Bean
    RestClient absencesRestClient(RestClient.Builder builder, BetterIntranetProperties properties) {
        return builder.baseUrl(properties.school().baseUrl()).build();
    }
}
