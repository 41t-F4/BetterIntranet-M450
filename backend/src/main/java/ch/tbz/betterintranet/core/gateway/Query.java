package ch.tbz.betterintranet.core.gateway;

/**
 * A query reads state without changing it. It is dispatched through the {@link QueryGateway}
 * to exactly one {@link QueryHandler}.
 *
 * @param <R> the result type returned by the handler
 */
public interface Query<R> {
}
