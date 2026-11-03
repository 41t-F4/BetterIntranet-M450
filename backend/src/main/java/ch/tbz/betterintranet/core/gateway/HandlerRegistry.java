package ch.tbz.betterintranet.core.gateway;

import ch.tbz.betterintranet.core.error.HandlerNotFoundException;
import ch.tbz.betterintranet.core.module.ModuleRegistry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.core.ResolvableType;

/**
 * Index of all command and query handlers, keyed by the message type they handle. The message type is
 * resolved from the generic type argument of the handler implementation, so a handler only has to
 * implement {@code QueryHandler<GetGrades, GradesResult>} to be found.
 */
public class HandlerRegistry {

    private final Map<Class<?>, CommandHandler<?, ?>> commandHandlers = new HashMap<>();
    private final Map<Class<?>, QueryHandler<?, ?>> queryHandlers = new HashMap<>();
    private final SortedSet<String> modules = new TreeSet<>();

    public HandlerRegistry(Collection<CommandHandler<?, ?>> commandHandlers,
                           Collection<QueryHandler<?, ?>> queryHandlers) {
        for (CommandHandler<?, ?> handler : commandHandlers) {
            register(this.commandHandlers, messageTypeOf(handler, CommandHandler.class), handler);
        }
        for (QueryHandler<?, ?> handler : queryHandlers) {
            register(this.queryHandlers, messageTypeOf(handler, QueryHandler.class), handler);
        }
    }

    @SuppressWarnings("unchecked")
    public <C extends Command<R>, R> CommandHandler<C, R> commandHandlerFor(Class<?> commandType) {
        CommandHandler<?, ?> handler = commandHandlers.get(commandType);
        if (handler == null) {
            throw new HandlerNotFoundException(commandType);
        }
        return (CommandHandler<C, R>) handler;
    }

    @SuppressWarnings("unchecked")
    public <Q extends Query<R>, R> QueryHandler<Q, R> queryHandlerFor(Class<?> queryType) {
        QueryHandler<?, ?> handler = queryHandlers.get(queryType);
        if (handler == null) {
            throw new HandlerNotFoundException(queryType);
        }
        return (QueryHandler<Q, R>) handler;
    }

    /** Names of the modules that own at least one registered handler, sorted. */
    public SortedSet<String> modules() {
        return Collections.unmodifiableSortedSet(modules);
    }

    private <H> void register(Map<Class<?>, H> target, Class<?> messageType, H handler) {
        H previous = target.putIfAbsent(messageType, handler);
        if (previous != null) {
            throw new IllegalStateException("Two handlers registered for " + messageType.getSimpleName() + ": "
                    + previous.getClass().getName() + " and " + handler.getClass().getName());
        }
        modules.add(ModuleRegistry.moduleOf(handler.getClass()));
    }

    private static Class<?> messageTypeOf(Object handler, Class<?> handlerInterface) {
        Class<?> messageType = ResolvableType.forClass(handler.getClass())
                .as(handlerInterface)
                .getGeneric(0)
                .resolve();
        if (messageType == null) {
            throw new IllegalStateException(handler.getClass().getName()
                    + " must declare a concrete message type for " + handlerInterface.getSimpleName());
        }
        return messageType;
    }
}
