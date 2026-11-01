package ch.tbz.schoolmock.repository;

/**
 * Thrown when a fixture file exists but cannot be read or parsed. This indicates a broken fixture
 * in the repository, not a wrong request, and is therefore mapped to HTTP 500 by the web layer.
 */
public class FixtureLoadException extends RuntimeException {

	public FixtureLoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
