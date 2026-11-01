package ch.tbz.betterintranet.core.error;

/** Thrown when no handler is registered for a command or query type. Indicates a wiring bug. */
public class HandlerNotFoundException extends RuntimeException {

    public HandlerNotFoundException(Class<?> messageType) {
        super("No handler registered for " + messageType.getName());
    }
}
