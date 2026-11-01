package ch.tbz.betterintranet.core.web;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/** Injects {@link CurrentUser} into controller methods. */
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String HEADER = "X-User";

    public CurrentUserArgumentResolver(String defaultUsername) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        throw new UnsupportedOperationException("not implemented");
    }
}
