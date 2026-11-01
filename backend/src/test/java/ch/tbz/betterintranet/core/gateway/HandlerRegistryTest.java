package ch.tbz.betterintranet.core.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.tbz.betterintranet.core.error.HandlerNotFoundException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HandlerRegistryTest {

    record Greet(String name) implements Query<String> {
    }

    record Count() implements Query<Integer> {
    }

    record Rename(String name) implements Command<String> {
    }

    static class GreetHandler implements QueryHandler<Greet, String> {
        @Override
        public String handle(Greet query) {
            return "hello " + query.name();
        }
    }

    static class CountHandler implements QueryHandler<Count, Integer> {
        @Override
        public Integer handle(Count query) {
            return 42;
        }
    }

    static class RenameHandler implements CommandHandler<Rename, String> {
        @Override
        public String handle(Rename command) {
            return "renamed to " + command.name();
        }
    }

    @Test
    @DisplayName("resolves the query handler from the generic type argument of the implementation")
    void resolvesQueryHandlerByMessageType() {
        var registry = new HandlerRegistry(List.of(), List.of(new GreetHandler(), new CountHandler()));

        QueryHandler<Greet, String> handler = registry.queryHandlerFor(Greet.class);

        assertThat(handler.handle(new Greet("Nicola"))).isEqualTo("hello Nicola");
        assertThat(registry.<Count, Integer>queryHandlerFor(Count.class).handle(new Count())).isEqualTo(42);
    }

    @Test
    @DisplayName("resolves the command handler from the generic type argument of the implementation")
    void resolvesCommandHandlerByMessageType() {
        var registry = new HandlerRegistry(List.of(new RenameHandler()), List.of());

        CommandHandler<Rename, String> handler = registry.commandHandlerFor(Rename.class);

        assertThat(handler.handle(new Rename("BetterIntranet"))).isEqualTo("renamed to BetterIntranet");
    }

    @Test
    @DisplayName("fails fast when no handler is registered for a message type")
    void throwsWhenNoHandlerRegistered() {
        var registry = new HandlerRegistry(List.of(), List.of(new GreetHandler()));

        assertThatThrownBy(() -> registry.queryHandlerFor(Count.class))
                .isInstanceOf(HandlerNotFoundException.class)
                .hasMessageContaining("Count");
        assertThatThrownBy(() -> registry.commandHandlerFor(Rename.class))
                .isInstanceOf(HandlerNotFoundException.class)
                .hasMessageContaining("Rename");
    }

    @Test
    @DisplayName("rejects two handlers for the same message type at construction time")
    void rejectsDuplicateHandlers() {
        assertThatThrownBy(() -> new HandlerRegistry(List.of(), List.of(new GreetHandler(), new GreetHandler())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Greet");
    }

    @Test
    @DisplayName("lists the modules of all registered handlers")
    void listsModulesOfRegisteredHandlers() {
        var registry = new HandlerRegistry(List.of(new RenameHandler()), List.of(new GreetHandler()));

        assertThat(registry.modules()).containsExactly("core");
    }
}
