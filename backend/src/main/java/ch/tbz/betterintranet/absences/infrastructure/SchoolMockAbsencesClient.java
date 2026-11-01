package ch.tbz.betterintranet.absences.infrastructure;

import ch.tbz.betterintranet.absences.api.Absence;
import ch.tbz.betterintranet.absences.application.AbsencesClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Reads absences from the {@code school-mock} service via {@code GET /mock/users/{username}/absences}. */
@Component
public class SchoolMockAbsencesClient implements AbsencesClient {

    private final RestClient restClient;

    public SchoolMockAbsencesClient(@Qualifier("absencesRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Absence> fetchAbsences(String username) {
        throw new UnsupportedOperationException("not implemented");
    }
}
