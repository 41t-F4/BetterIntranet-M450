package ch.tbz.betterintranet.core.gateway;

/**
 * Handles exactly one {@link Command} type. Implementations are Spring beans inside a feature module
 * and are discovered automatically by the {@link HandlerRegistry}.
 */
public interface CommandHandler<C extends Command<R>, R> {

    R handle(C command);
}
