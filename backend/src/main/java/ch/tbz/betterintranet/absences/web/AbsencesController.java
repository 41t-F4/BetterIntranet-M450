package ch.tbz.betterintranet.absences.web;

import ch.tbz.betterintranet.absences.api.AbsenceStatus;
import ch.tbz.betterintranet.absences.api.AbsencesOverview;
import ch.tbz.betterintranet.absences.api.GetAbsencesQuery;
import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.core.web.CurrentUser;
import java.net.URI;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** {@code GET /api/absences}: absences of the current user, optionally filtered by {@code ?status=OPEN}. */
@RestController
@RequestMapping("/api/absences")
public class AbsencesController {

    private final QueryGateway queries;

    public AbsencesController(QueryGateway queries) {
        this.queries = queries;
    }

    @GetMapping
    public AbsencesOverview absences(CurrentUser user, @RequestParam(required = false) AbsenceStatus status) {
        return queries.ask(new GetAbsencesQuery(user.username(), status));
    }

    /** A {@code status} value outside {@link AbsenceStatus} is a client error, not a server error. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ProblemDetail invalidParameter(MethodArgumentTypeMismatchException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'. Allowed values: "
                        + Arrays.toString(AbsenceStatus.values()));
        problem.setType(URI.create("urn:betterintranet:invalid-parameter"));
        problem.setTitle("Invalid parameter");
        return problem;
    }
}
