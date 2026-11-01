package ch.tbz.betterintranet.classes.application;

import ch.tbz.betterintranet.classes.api.SchoolClassMembership;
import java.util.List;

/** Port to the school system that knows which classes a user belongs to. */
public interface ClassesClient {

    /**
     * @throws ch.tbz.betterintranet.core.error.NotFoundException            if the user is unknown
     * @throws ch.tbz.betterintranet.core.error.UpstreamUnavailableException if the school system cannot be reached
     */
    List<SchoolClassMembership> classesOf(String username);
}
