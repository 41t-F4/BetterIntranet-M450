package ch.tbz.betterintranet.core.gateway;

/**
 * Handles exactly one {@link Query} type. Implementations are Spring beans inside a feature module
 * and are discovered automatically by the {@link HandlerRegistry}.
 */
public interface QueryHandler<Q extends Query<R>, R> {

    R handle(Q query);
}
