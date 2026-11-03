package ch.tbz.betterintranet.core.error;

/** Thrown by handlers when a requested resource does not exist. Mapped to HTTP 404. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
