package ch.tbz.betterintranet.courses.infrastructure;

import ch.tbz.betterintranet.core.config.BetterIntranetProperties;
import ch.tbz.betterintranet.courses.application.CoursesClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/** Wires the module-private HTTP client of the courses module. */
@Configuration
public class CoursesInfrastructureConfiguration {

    @Bean
    RestClient coursesRestClient(RestClient.Builder builder, BetterIntranetProperties properties) {
        return builder.baseUrl(properties.school().baseUrl()).build();
    }

    @Bean
    CoursesClient schoolMockCoursesClient(RestClient coursesRestClient) {
        return new SchoolMockCoursesClient(coursesRestClient);
    }
}
