package ch.tbz.betterintranet.core.gateway;

import ch.tbz.betterintranet.core.error.ModuleDisabledException;
import ch.tbz.betterintranet.core.module.ModuleRegistry;
import java.util.Objects;

/** Default {@link QueryGateway} backed by the {@link HandlerRegistry} and {@link ModuleRegistry}. */
public class DefaultQueryGateway implements QueryGateway {

    private final HandlerRegistry handlers;
    private final ModuleRegistry modules;

    public DefaultQueryGateway(HandlerRegistry handlers, ModuleRegistry modules) {
        this.handlers = Objects.requireNonNull(handlers);
        this.modules = Objects.requireNonNull(modules);
    }

    @Override
    public <R> R ask(Query<R> query) {
        Objects.requireNonNull(query, "query must not be null");
        QueryHandler<Query<R>, R> handler = handlers.queryHandlerFor(query.getClass());
        String module = ModuleRegistry.moduleOf(handler.getClass());
        if (!modules.isEnabled(module)) {
            throw new ModuleDisabledException(module);
        }
        return handler.handle(query);
    }
}
