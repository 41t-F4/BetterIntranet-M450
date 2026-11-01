package ch.tbz.betterintranet.user.application;

import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.user.api.GetUserProfileQuery;
import ch.tbz.betterintranet.user.api.UserProfile;
import org.springframework.stereotype.Component;

/** Answers {@link GetUserProfileQuery}; an unknown username is a {@link NotFoundException} (HTTP 404). */
@Component
public class GetUserProfileHandler implements QueryHandler<GetUserProfileQuery, UserProfile> {

    private final UserProfileRepository users;

    public GetUserProfileHandler(UserProfileRepository users) {
        this.users = users;
    }

    @Override
    public UserProfile handle(GetUserProfileQuery query) {
        return users.findByUsername(query.username())
                .orElseThrow(() -> new NotFoundException("User " + query.username() + " not found"));
    }
}
