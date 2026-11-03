package ch.tbz.betterintranet.core.gateway;

import ch.tbz.betterintranet.core.error.ModuleDisabledException;
import ch.tbz.betterintranet.core.module.ModuleRegistry;
import java.util.Objects;

/** Default {@link CommandGateway} backed by the {@link HandlerRegistry} and {@link ModuleRegistry}. */
public class DefaultCommandGateway implements CommandGateway {

    private final HandlerRegistry handlers;
    private final ModuleRegistry modules;

    public DefaultCommandGateway(HandlerRegistry handlers, ModuleRegistry modules) {
        this.handlers = Objects.requireNonNull(handlers);
        this.modules = Objects.requireNonNull(modules);
    }

    @Override
    public <R> R send(Command<R> command) {
        Objects.requireNonNull(command, "command must not be null");
        CommandHandler<Command<R>, R> handler = handlers.commandHandlerFor(command.getClass());
        String module = ModuleRegistry.moduleOf(handler.getClass());
        if (!modules.isEnabled(module)) {
            throw new ModuleDisabledException(module);
        }
        return handler.handle(command);
    }
}
