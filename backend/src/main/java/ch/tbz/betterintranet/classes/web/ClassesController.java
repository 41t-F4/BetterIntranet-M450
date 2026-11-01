package ch.tbz.betterintranet.classes.web;

import ch.tbz.betterintranet.classes.api.GetMyClassesQuery;
import ch.tbz.betterintranet.classes.api.SchoolClassMembership;
import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.core.web.CurrentUser;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** {@code GET /api/classes}: the classes of the current user. */
@RestController
@RequestMapping("/api/classes")
public class ClassesController {

    private final QueryGateway queries;

    public ClassesController(QueryGateway queries) {
        this.queries = queries;
    }

    @GetMapping
    public List<SchoolClassMembership> myClasses(CurrentUser user) {
        return queries.ask(new GetMyClassesQuery(user.username()));
    }
}
