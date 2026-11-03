package ch.tbz.betterintranet.core.gateway;

/**
 * A command expresses an intent to change state. It is dispatched through the {@link CommandGateway}
 * to exactly one {@link CommandHandler}.
 *
 * @param <R> the result type returned by the handler
 */
public interface Command<R> {
}
