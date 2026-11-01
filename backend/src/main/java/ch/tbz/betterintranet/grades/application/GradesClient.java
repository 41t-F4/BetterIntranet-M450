package ch.tbz.betterintranet.grades.application;

import java.util.List;

/** Port to the school system that stores grades. Implemented in {@code infrastructure}, mocked in tests. */
public interface GradesClient {

    /**
     * @throws ch.tbz.betterintranet.core.error.NotFoundException            when the user is unknown upstream
     * @throws ch.tbz.betterintranet.core.error.UpstreamUnavailableException when the school system cannot be reached or answers with an error
     */
    List<RawGrade> fetchGrades(String username);
}
