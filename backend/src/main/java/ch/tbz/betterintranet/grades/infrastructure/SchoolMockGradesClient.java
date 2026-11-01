package ch.tbz.betterintranet.grades.infrastructure;

import ch.tbz.betterintranet.grades.application.GradesClient;
import ch.tbz.betterintranet.grades.application.RawGrade;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** {@link GradesClient} against the {@code school-mock} service: {@code GET /mock/users/{username}/grades}. */
@Component
public class SchoolMockGradesClient implements GradesClient {

    private final RestClient restClient;

    public SchoolMockGradesClient(@Qualifier("gradesRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<RawGrade> fetchGrades(String username) {
        throw new UnsupportedOperationException("not implemented");
    }
}
