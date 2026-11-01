package ch.tbz.schoolmock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the school-mock service.
 *
 * <p>BetterIntranet has no access to the real school systems of the TBZ (class administration,
 * course enrolment, absences, grades). This second, standalone Spring Boot service stands in for
 * them on port 8081 and serves static per-user fixture data from the classpath under
 * {@code data/users/<username>/}. It is read-only and has no persistence; the main backend talks
 * to it over HTTP exactly as it would talk to the real systems, which keeps the real integration
 * swappable later on.
 */
@SpringBootApplication
public class SchoolMockApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolMockApplication.class, args);
	}

}
