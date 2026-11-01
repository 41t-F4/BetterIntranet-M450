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

class DefaultCommandGatewayTest {

    record Rename(String name) implements Command<String> {
    }

    private HandlerRegistry handlers;
    private ModuleRegistry modules;
    private CommandHandler<Rename, String> renameHandler;
    private DefaultCommandGateway gateway;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        handlers = mock(HandlerRegistry.class);
        modules = mock(ModuleRegistry.class);
        renameHandler = mock(CommandHandler.class);
        when(handlers.<Rename, String>commandHandlerFor(Rename.class)).thenReturn(renameHandler);
        gateway = new DefaultCommandGateway(handlers, modules);
    }

    @Test
    @DisplayName("dispatches the command to its handler when the module is enabled")
    void dispatchesToHandler() {
        when(modules.isEnabled("core")).thenReturn(true);
        when(renameHandler.handle(new Rename("x"))).thenReturn("renamed to x");

        assertThat(gateway.send(new Rename("x"))).isEqualTo("renamed to x");
    }

    @Test
    @DisplayName("refuses to dispatch when the handler's module is disabled")
    void refusesWhenModuleDisabled() {
        when(modules.isEnabled("core")).thenReturn(false);

        assertThatThrownBy(() -> gateway.send(new Rename("x")))
                .isInstanceOf(ModuleDisabledException.class);
        verify(renameHandler, never()).handle(any());
    }

    @Test
    @DisplayName("rejects null commands")
    void rejectsNull() {
        assertThatThrownBy(() -> gateway.send(null)).isInstanceOf(NullPointerException.class);
    }
}
