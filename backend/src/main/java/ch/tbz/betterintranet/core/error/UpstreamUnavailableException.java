package ch.tbz.betterintranet.core.error;

/** Thrown when an external system (timetable, school mock) cannot be reached or answers with an error. Mapped to HTTP 502. */
public class UpstreamUnavailableException extends RuntimeException {

    private final String upstream;

    public UpstreamUnavailableException(String upstream, String message, Throwable cause) {
        super(upstream + ": " + message, cause);
        this.upstream = upstream;
    }

    public UpstreamUnavailableException(String upstream, String message) {
        this(upstream, message, null);
    }

    public String upstream() {
        return upstream;
    }
}
