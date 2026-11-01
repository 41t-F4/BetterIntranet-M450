package ch.tbz.betterintranet.grades.infrastructure;

import ch.tbz.betterintranet.core.config.BetterIntranetProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/** Module-private HTTP client pointing at the school system ({@code betterintranet.school.base-url}). */
@Configuration
public class GradesClientConfiguration {

    @Bean
    RestClient gradesRestClient(RestClient.Builder builder, BetterIntranetProperties properties) {
        return builder.baseUrl(properties.school().baseUrl()).build();
    }
}
