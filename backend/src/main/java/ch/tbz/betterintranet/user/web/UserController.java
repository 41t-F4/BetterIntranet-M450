package ch.tbz.betterintranet.user.web;

import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.core.web.CurrentUser;
import ch.tbz.betterintranet.user.api.GetUserProfileQuery;
import ch.tbz.betterintranet.user.api.UserProfile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** {@code GET /api/me}: the profile of the current user (from the {@code X-User} header). */
@RestController
@RequestMapping("/api/me")
public class UserController {

    private final QueryGateway queries;

    public UserController(QueryGateway queries) {
        this.queries = queries;
    }

    @GetMapping
    public UserProfile me(CurrentUser user) {
        return queries.ask(new GetUserProfileQuery(user.username()));
    }
}
