package ch.tbz.betterintranet.core.gateway;

import ch.tbz.betterintranet.core.module.ModuleRegistry;

/** Default {@link QueryGateway} backed by the {@link HandlerRegistry} and {@link ModuleRegistry}. */
public class DefaultQueryGateway implements QueryGateway {

    public DefaultQueryGateway(HandlerRegistry handlers, ModuleRegistry modules) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public <R> R ask(Query<R> query) {
        throw new UnsupportedOperationException("not implemented");
    }
}
