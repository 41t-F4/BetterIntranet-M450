package ch.tbz.betterintranet.absences.infrastructure;

import ch.tbz.betterintranet.absences.api.Absence;
import ch.tbz.betterintranet.absences.application.AbsencesClient;
import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Reads absences from the {@code school-mock} service via {@code GET /mock/users/{username}/absences}.
 * A 404 means the user is unknown; every other failure (error status, unreachable host, unparseable body)
 * is reported as {@link UpstreamUnavailableException} for the upstream {@value #UPSTREAM}.
 */
@Component
public class SchoolMockAbsencesClient implements AbsencesClient {

    static final String UPSTREAM = "school";

    private static final ParameterizedTypeReference<List<AbsenceDto>> ABSENCE_LIST =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public SchoolMockAbsencesClient(@Qualifier("absencesRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Absence> fetchAbsences(String username) {
        try {
            List<AbsenceDto> dtos = restClient.get()
                    .uri("/mock/users/{username}/absences", username)
                    .retrieve()
                    .body(ABSENCE_LIST);
            return dtos == null ? List.of() : dtos.stream().map(AbsenceDto::toAbsence).toList();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NotFoundException("User '" + username + "' not found");
        } catch (RestClientResponseException ex) {
            throw new UpstreamUnavailableException(UPSTREAM,
                    "unexpected status " + ex.getStatusCode().value() + " when loading absences", ex);
        } catch (RestClientException ex) {
            throw new UpstreamUnavailableException(UPSTREAM, "absences could not be loaded: " + ex.getMessage(), ex);
        }
    }
}
