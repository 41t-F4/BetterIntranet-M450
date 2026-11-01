package ch.tbz.betterintranet.grades.infrastructure;

import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import ch.tbz.betterintranet.grades.application.GradesClient;
import ch.tbz.betterintranet.grades.application.RawGrade;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** {@link GradesClient} against the {@code school-mock} service: {@code GET /mock/users/{username}/grades}. */
@Component
public class SchoolMockGradesClient implements GradesClient {

    static final String UPSTREAM = "school";

    private static final ParameterizedTypeReference<List<SchoolGradeDto>> GRADE_LIST =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public SchoolMockGradesClient(@Qualifier("gradesRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<RawGrade> fetchGrades(String username) {
        List<SchoolGradeDto> grades;
        try {
            grades = restClient.get()
                    .uri("/mock/users/{username}/grades", username)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(GRADE_LIST);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("User " + username + " not found");
        } catch (RestClientException e) {
            throw new UpstreamUnavailableException(UPSTREAM, "could not load grades of " + username, e);
        }
        return grades == null ? List.of() : grades.stream().map(SchoolGradeDto::toRawGrade).toList();
    }
}
