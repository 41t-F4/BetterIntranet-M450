package ch.tbz.schoolmock.repository;

/**
 * Thrown when no fixture data exists for a username. Mapped to HTTP 404 by the web layer.
 */
public class UserNotFoundException extends RuntimeException {

	private final String username;

	public UserNotFoundException(String username) {
		super("No fixture data for user '" + username + "'");
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
