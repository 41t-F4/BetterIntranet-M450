package ch.tbz.betterintranet.core.web;

/**
 * The user making the request. Resolved from the {@code X-User} header because the school uses SSO,
 * which is out of scope; without the header the configured default user is used.
 */
public record CurrentUser(String username) {
}
