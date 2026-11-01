package ch.tbz.betterintranet.core.web;

import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/** Injects {@link CurrentUser} into controller methods from the {@code X-User} header. */
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String HEADER = "X-User";

    private final String defaultUsername;

    public CurrentUserArgumentResolver(String defaultUsername) {
        this.defaultUsername = Objects.requireNonNull(defaultUsername, "defaultUsername");
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return CurrentUser.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String header = webRequest.getHeader(HEADER);
        String username = header == null || header.isBlank() ? defaultUsername : header.trim();
        return new CurrentUser(username);
    }
}
