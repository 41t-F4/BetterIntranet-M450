package ch.tbz.betterintranet.core.gateway;

import java.util.Collection;

/**
 * Index of all command and query handlers, keyed by the message type they handle. The message type is
 * resolved from the generic type argument of the handler implementation.
 */
public class HandlerRegistry {

    public HandlerRegistry(Collection<CommandHandler<?, ?>> commandHandlers,
                           Collection<QueryHandler<?, ?>> queryHandlers) {
        throw new UnsupportedOperationException("not implemented");
    }

    @SuppressWarnings("unchecked")
    public <C extends Command<R>, R> CommandHandler<C, R> commandHandlerFor(Class<? extends Command<?>> commandType) {
        throw new UnsupportedOperationException("not implemented");
    }

    @SuppressWarnings("unchecked")
    public <Q extends Query<R>, R> QueryHandler<Q, R> queryHandlerFor(Class<? extends Query<?>> queryType) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** Names of the modules that own at least one registered handler, sorted. */
    public java.util.SortedSet<String> modules() {
        throw new UnsupportedOperationException("not implemented");
    }
}
