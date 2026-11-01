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
		return problem(HttpStatus.NOT_FOUND, USER_NOT_FOUND_TYPE, "User not found", exception.getMessage());
	}

	@ExceptionHandler(FixtureLoadException.class)
	public ProblemDetail handleFixtureLoad(FixtureLoadException exception) {
		return problem(HttpStatus.INTERNAL_SERVER_ERROR, FIXTURE_UNREADABLE_TYPE, "Fixture data unreadable",
				exception.getMessage());
	}

	private static ProblemDetail problem(HttpStatus status, URI type, String title, String detail) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
		problem.setType(type);
		problem.setTitle(title);
		return problem;
	}
}
