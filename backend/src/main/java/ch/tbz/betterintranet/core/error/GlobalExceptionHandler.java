package ch.tbz.betterintranet.core.error;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps domain and gateway exceptions to RFC 9457 problem details. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ModuleDisabledException.class)
    public ProblemDetail moduleDisabled(ModuleDisabledException ex) {
        throw new UnsupportedOperationException("not implemented");
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail notFound(NotFoundException ex) {
        throw new UnsupportedOperationException("not implemented");
    }

    @ExceptionHandler(UpstreamUnavailableException.class)
    public ProblemDetail upstreamUnavailable(UpstreamUnavailableException ex) {
        throw new UnsupportedOperationException("not implemented");
    }

    @ExceptionHandler(HandlerNotFoundException.class)
    public ProblemDetail handlerNotFound(HandlerNotFoundException ex) {
        throw new UnsupportedOperationException("not implemented");
    }
}
