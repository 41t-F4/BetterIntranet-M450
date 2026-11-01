package ch.tbz.betterintranet.core.gateway;

import ch.tbz.betterintranet.core.module.ModuleRegistry;

/** Default {@link CommandGateway} backed by the {@link HandlerRegistry} and {@link ModuleRegistry}. */
public class DefaultCommandGateway implements CommandGateway {

    public DefaultCommandGateway(HandlerRegistry handlers, ModuleRegistry modules) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public <R> R send(Command<R> command) {
        throw new UnsupportedOperationException("not implemented");
    }
}
