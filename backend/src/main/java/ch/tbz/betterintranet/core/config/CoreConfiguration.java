package ch.tbz.betterintranet.core.config;

import ch.tbz.betterintranet.core.gateway.CommandGateway;
import ch.tbz.betterintranet.core.gateway.CommandHandler;
import ch.tbz.betterintranet.core.gateway.DefaultCommandGateway;
import ch.tbz.betterintranet.core.gateway.DefaultQueryGateway;
import ch.tbz.betterintranet.core.gateway.HandlerRegistry;
import ch.tbz.betterintranet.core.gateway.QueryGateway;
import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.core.module.ModuleRegistry;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Wires the gateways: every {@link CommandHandler} and {@link QueryHandler} bean is picked up automatically. */
@Configuration
@EnableCaching
@EnableConfigurationProperties(BetterIntranetProperties.class)
public class CoreConfiguration {

    @Bean
    HandlerRegistry handlerRegistry(List<CommandHandler<?, ?>> commandHandlers,
                                    List<QueryHandler<?, ?>> queryHandlers) {
        return new HandlerRegistry(commandHandlers, queryHandlers);
    }

    @Bean
    ModuleRegistry moduleRegistry(BetterIntranetProperties properties, HandlerRegistry handlers) {
        return new ModuleRegistry(properties.modules(), handlers.modules());
    }

    @Bean
    CommandGateway commandGateway(HandlerRegistry handlers, ModuleRegistry modules) {
        return new DefaultCommandGateway(handlers, modules);
    }

    @Bean
    QueryGateway queryGateway(HandlerRegistry handlers, ModuleRegistry modules) {
        return new DefaultQueryGateway(handlers, modules);
    }
}
