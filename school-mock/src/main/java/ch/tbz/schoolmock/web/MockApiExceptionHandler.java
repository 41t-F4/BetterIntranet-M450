package ch.tbz.schoolmock.web;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ch.tbz.schoolmock.repository.FixtureLoadException;
import ch.tbz.schoolmock.repository.UserNotFoundException;

/**
 * Maps repository exceptions to RFC 9457 {@link ProblemDetail} responses
 * ({@code application/problem+json}). Spring fills in the {@code instance} field with the request
 * path.
 */
@RestControllerAdvice
public class MockApiExceptionHandler {

	static final URI USER_NOT_FOUND_TYPE = URI.create("urn:betterintranet:school-mock:user-not-found");

	static final URI FIXTURE_UNREADABLE_TYPE = URI.create("urn:betterintranet:school-mock:fixture-unreadable");

	@ExceptionHandler(UserNotFoundException.class)
	public ProblemDetail handleUserNotFound(UserNotFoundException exception) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
		problem.setType(USER_NOT_FOUND_TYPE);
		problem.setTitle("User not found");
		return problem;
	}

	@ExceptionHandler(FixtureLoadException.class)
	public ProblemDetail handleFixtureLoad(FixtureLoadException exception) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				exception.getMessage());
		problem.setType(FIXTURE_UNREADABLE_TYPE);
		problem.setTitle("Fixture data unreadable");
		return problem;
	}
}
