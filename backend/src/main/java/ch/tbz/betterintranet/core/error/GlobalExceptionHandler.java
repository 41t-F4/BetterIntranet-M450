package ch.tbz.betterintranet.core.error;

import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Renders every error as an RFC 9457 problem detail. Domain and gateway exceptions get their own types;
 * Spring MVC's standard exceptions (missing or mistyped parameter, unknown path, wrong method, ...) keep
 * their status through {@link ResponseEntityExceptionHandler}; anything else becomes a 500 that does not
 * leak internal messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String TYPE_PREFIX = "urn:betterintranet:";

    @ExceptionHandler(ModuleDisabledException.class)
    public ProblemDetail moduleDisabled(ModuleDisabledException ex) {
        ProblemDetail problem = problem(HttpStatus.SERVICE_UNAVAILABLE, "module-disabled", "Module disabled",
                "The module '" + ex.moduleName() + "' is currently switched off.");
        problem.setProperty("module", ex.moduleName());
        return problem;
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ProblemDetail invalidRequest(InvalidRequestException ex) {
        return problem(HttpStatus.BAD_REQUEST, "invalid-request", "Invalid request", ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail notFound(NotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "not-found", "Not found", ex.getMessage());
    }

    @ExceptionHandler(UpstreamUnavailableException.class)
    public ProblemDetail upstreamUnavailable(UpstreamUnavailableException ex) {
        log.warn("Upstream '{}' unavailable: {}", ex.upstream(), ex.getMessage(), ex);
        ProblemDetail problem = problem(HttpStatus.BAD_GATEWAY, "upstream-unavailable", "Upstream unavailable",
                ex.getMessage());
        problem.setProperty("upstream", ex.upstream());
        return problem;
    }

    @ExceptionHandler(HandlerNotFoundException.class)
    public ProblemDetail handlerNotFound(HandlerNotFoundException ex) {
        log.error("Gateway wiring error", ex);
        return internalError();
    }

    /** Last line of defence: unexpected exceptions are logged and answered without details. */
    @ExceptionHandler(Exception.class)
    public ProblemDetail unexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return internalError();
    }

    private static ProblemDetail internalError() {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "internal", "Internal error",
                "The request could not be processed.");
    }

    private static ProblemDetail problem(HttpStatus status, String type, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create(TYPE_PREFIX + type));
        problem.setTitle(title);
        return problem;
    }
}
