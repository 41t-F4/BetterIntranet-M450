package ch.tbz.betterintranet.core.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

class CurrentUserArgumentResolverTest {

    private final CurrentUserArgumentResolver resolver = new CurrentUserArgumentResolver("nicola.wehrli");

    @SuppressWarnings("unused")
    void sample(CurrentUser user, String other) {
    }

    private MethodParameter parameter(int index) throws NoSuchMethodException {
        return new MethodParameter(getClass().getDeclaredMethod("sample", CurrentUser.class, String.class), index);
    }

    @Test
    @DisplayName("supports only CurrentUser parameters")
    void supportsCurrentUserOnly() throws Exception {
        assertThat(resolver.supportsParameter(parameter(0))).isTrue();
        assertThat(resolver.supportsParameter(parameter(1))).isFalse();
    }

    @Test
    @DisplayName("uses the X-User header when present")
    void usesHeader() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("X-User", "max.muster");

        Object resolved = resolver.resolveArgument(parameter(0), null, new ServletWebRequest(request), null);

        assertThat(resolved).isEqualTo(new CurrentUser("max.muster"));
    }

    @Test
    @DisplayName("falls back to the configured default user when the header is missing or blank")
    void fallsBackToDefault() throws Exception {
        var blank = new MockHttpServletRequest();
        blank.addHeader("X-User", "  ");

        assertThat(resolver.resolveArgument(parameter(0), null, new ServletWebRequest(new MockHttpServletRequest()), null))
                .isEqualTo(new CurrentUser("nicola.wehrli"));
        assertThat(resolver.resolveArgument(parameter(0), null, new ServletWebRequest(blank), null))
                .isEqualTo(new CurrentUser("nicola.wehrli"));
    }
}
