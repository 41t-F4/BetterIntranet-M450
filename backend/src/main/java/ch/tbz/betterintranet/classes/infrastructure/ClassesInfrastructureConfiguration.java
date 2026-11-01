package ch.tbz.betterintranet.classes.infrastructure;

import ch.tbz.betterintranet.classes.application.ClassesClient;
import ch.tbz.betterintranet.core.config.BetterIntranetProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/** Wires the module-private HTTP client of the classes module. */
@Configuration
public class ClassesInfrastructureConfiguration {

    @Bean
    RestClient classesRestClient(RestClient.Builder builder, BetterIntranetProperties properties) {
        return builder.baseUrl(properties.school().baseUrl()).build();
    }

    @Bean
    ClassesClient schoolMockClassesClient(RestClient classesRestClient) {
        return new SchoolMockClassesClient(classesRestClient);
    }
}
