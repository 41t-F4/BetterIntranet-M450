package ch.tbz.betterintranet.core.error;

/**
 * Thrown when a request is syntactically fine but violates a rule of the domain (e.g. a date range that is
 * inverted or too long). Mapped to HTTP 400. The message is written for API clients and is returned as the
 * problem detail, so it must not contain internals.
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
