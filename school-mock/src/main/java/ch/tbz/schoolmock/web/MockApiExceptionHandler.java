package ch.tbz.schoolmock.web;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ch.tbz.schoolmock.repository.FixtureLoadException;
import ch.tbz.schoolmock.repository.UserNotFoundException;

/**
 * Answers every error with an RFC 9457 {@link ProblemDetail} ({@code application/problem+json}).
 * Repository exceptions get their own types; Spring MVC's standard exceptions (unknown path, wrong
 * method, ...) keep their status through {@link ResponseEntityExceptionHandler}; anything else becomes
 * a generic 500 that does not leak internal messages. Spring fills in the {@code instance} field with
 * the request path.
 */
@RestControllerAdvice
public class MockApiExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(MockApiExceptionHandler.class);

	static final URI INTERNAL_ERROR_TYPE = URI.create("urn:betterintranet:school-mock:internal-error");

	static final URI USER_NOT_FOUND_TYPE = URI.create("urn:betterintranet:school-mock:user-not-found");

	static final URI FIXTURE_UNREADABLE_TYPE = URI.create("urn:betterintranet:school-mock:fixture-unreadable");

	@ExceptionHandler(UserNotFoundException.class)
	public ProblemDetail handleUserNotFound(UserNotFoundException exception) {
		return problem(HttpStatus.NOT_FOUND, USER_NOT_FOUND_TYPE, "User not found", exception.getMessage());
	}

	@ExceptionHandler(FixtureLoadException.class)
	public ProblemDetail handleFixtureLoad(FixtureLoadException exception) {
		return problem(HttpStatus.INTERNAL_SERVER_ERROR, FIXTURE_UNREADABLE_TYPE, "Fixture data unreadable",
				exception.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleUnexpected(Exception exception) {
		LOG.error("Unexpected error while serving mock data", exception);
		return problem(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR_TYPE, "Internal error",
				"The request could not be processed.");
	}

	private static ProblemDetail problem(HttpStatus status, URI type, String title, String detail) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
		problem.setType(type);
		problem.setTitle(title);
		return problem;
	}
}
