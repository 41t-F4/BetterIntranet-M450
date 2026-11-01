package ch.tbz.betterintranet.core.config;

import ch.tbz.betterintranet.core.web.CurrentUserArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Registers the {@code X-User} argument resolver. */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final BetterIntranetProperties properties;

    public WebConfiguration(BetterIntranetProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver(properties.defaultUser()));
    }
}
