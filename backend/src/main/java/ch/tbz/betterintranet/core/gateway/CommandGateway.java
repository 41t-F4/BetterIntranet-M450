package ch.tbz.betterintranet.core.gateway;

/**
 * Single entry point for sending commands between modules. Callers never reference a handler directly;
 * the gateway resolves it, checks that the owning module is enabled and dispatches.
 */
public interface CommandGateway {

    <R> R send(Command<R> command);
}
