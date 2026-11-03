package ch.tbz.betterintranet.core.error;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("a disabled module becomes 503 with the module name in the body")
    void moduleDisabled() {
        ProblemDetail problem = handler.moduleDisabled(new ModuleDisabledException("grades"));

        assertThat(problem.getStatus()).isEqualTo(503);
        assertThat(problem.getType()).isEqualTo(URI.create("urn:betterintranet:module-disabled"));
        assertThat(problem.getTitle()).isEqualTo("Module disabled");
        assertThat(problem.getDetail()).contains("grades");
        assertThat(problem.getProperties()).containsEntry("module", "grades");
    }

    @Test
    @DisplayName("a missing resource becomes 404")
    void notFound() {
        ProblemDetail problem = handler.notFound(new NotFoundException("Class 999 not found"));

        assertThat(problem.getStatus()).isEqualTo(404);
        assertThat(problem.getType()).isEqualTo(URI.create("urn:betterintranet:not-found"));
        assertThat(problem.getDetail()).isEqualTo("Class 999 not found");
    }

    @Test
    @DisplayName("an unreachable upstream becomes 502 and names the upstream")
    void upstreamUnavailable() {
        ProblemDetail problem = handler.upstreamUnavailable(
                new UpstreamUnavailableException("timetable", "connection refused"));

        assertThat(problem.getStatus()).isEqualTo(502);
        assertThat(problem.getType()).isEqualTo(URI.create("urn:betterintranet:upstream-unavailable"));
        assertThat(problem.getProperties()).containsEntry("upstream", "timetable");
        assertThat(problem.getDetail()).contains("connection refused");
    }

    @Test
    @DisplayName("a missing handler is an internal error that does not leak class names")
    void handlerNotFound() {
        ProblemDetail problem = handler.handlerNotFound(new HandlerNotFoundException(String.class));

        assertThat(problem.getStatus()).isEqualTo(500);
        assertThat(problem.getDetail()).doesNotContain("java.lang.String");
    }

    @Test
    @DisplayName("an invalid request becomes 400 with the client-facing message")
    void invalidRequest() {
        ProblemDetail problem = handler.invalidRequest(new InvalidRequestException("'to' must not be before 'from'"));

        assertThat(problem.getStatus()).isEqualTo(400);
        assertThat(problem.getType()).isEqualTo(URI.create("urn:betterintranet:invalid-request"));
        assertThat(problem.getTitle()).isEqualTo("Invalid request");
        assertThat(problem.getDetail()).isEqualTo("'to' must not be before 'from'");
    }

    @Test
    @DisplayName("any other exception becomes 500 without leaking its message")
    void unexpected() {
        ProblemDetail problem = handler.unexpected(new IllegalStateException("db password is hunter2"));

        assertThat(problem.getStatus()).isEqualTo(500);
        assertThat(problem.getType()).isEqualTo(URI.create("urn:betterintranet:internal"));
        assertThat(problem.getDetail()).doesNotContain("hunter2");
    }
}
