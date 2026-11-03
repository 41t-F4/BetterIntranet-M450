package ch.tbz.betterintranet.core.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.tbz.betterintranet.core.error.ModuleDisabledException;
import ch.tbz.betterintranet.core.module.ModuleRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultQueryGatewayTest {

    record Greet(String name) implements Query<String> {
    }

    private HandlerRegistry handlers;
    private ModuleRegistry modules;
    private QueryHandler<Greet, String> greetHandler;
    private DefaultQueryGateway gateway;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        handlers = mock(HandlerRegistry.class);
        modules = mock(ModuleRegistry.class);
        greetHandler = mock(QueryHandler.class);
        when(handlers.<Greet, String>queryHandlerFor(Greet.class)).thenReturn(greetHandler);
        gateway = new DefaultQueryGateway(handlers, modules);
    }

    @Test
    @DisplayName("dispatches the query to its handler when the module is enabled")
    void dispatchesToHandler() {
        when(modules.isEnabled("core")).thenReturn(true);
        when(greetHandler.handle(new Greet("Nicola"))).thenReturn("hello Nicola");

        String result = gateway.ask(new Greet("Nicola"));

        assertThat(result).isEqualTo("hello Nicola");
    }

    @Test
    @DisplayName("refuses to dispatch when the handler's module is disabled")
    void refusesWhenModuleDisabled() {
        when(modules.isEnabled("core")).thenReturn(false);

        assertThatThrownBy(() -> gateway.ask(new Greet("Nicola")))
                .isInstanceOf(ModuleDisabledException.class)
                .extracting("moduleName").isEqualTo("core");
        verify(greetHandler, never()).handle(any());
    }

    @Test
    @DisplayName("rejects null queries")
    void rejectsNull() {
        assertThatThrownBy(() -> gateway.ask(null)).isInstanceOf(NullPointerException.class);
    }
}
