package ch.tbz.betterintranet.absences.application;

import ch.tbz.betterintranet.absences.api.Absence;
import java.util.List;

/** Port to the school system that stores absences. Implemented in the infrastructure package, mocked in tests. */
public interface AbsencesClient {

    /**
     * @throws ch.tbz.betterintranet.core.error.NotFoundException            if the user is unknown upstream
     * @throws ch.tbz.betterintranet.core.error.UpstreamUnavailableException if the school system cannot be reached
     */
    List<Absence> fetchAbsences(String username);
}
