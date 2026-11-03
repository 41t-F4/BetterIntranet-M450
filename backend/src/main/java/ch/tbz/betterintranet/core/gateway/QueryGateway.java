package ch.tbz.betterintranet.core.gateway;

/**
 * Single entry point for asking queries between modules. Callers never reference a handler directly;
 * the gateway resolves it, checks that the owning module is enabled and dispatches.
 */
public interface QueryGateway {

    <R> R ask(Query<R> query);
}
