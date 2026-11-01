package ch.tbz.schoolmock.web;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ch.tbz.schoolmock.repository.FixtureLoadException;
import ch.tbz.schoolmock.repository.UserNotFoundException;

/**
 * Maps repository exceptions to RFC 9457 {@link ProblemDetail} responses.
 */
@RestControllerAdvice
public class MockApiExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	public ProblemDetail handleUserNotFound(UserNotFoundException exception) {
		throw new UnsupportedOperationException("not implemented");
	}

	@ExceptionHandler(FixtureLoadException.class)
	public ProblemDetail handleFixtureLoad(FixtureLoadException exception) {
		throw new UnsupportedOperationException("not implemented");
	}
}
