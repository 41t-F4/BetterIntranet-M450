package ch.tbz.schoolmock.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.tbz.schoolmock.api.Absence;
import ch.tbz.schoolmock.api.Course;
import ch.tbz.schoolmock.api.Grade;
import ch.tbz.schoolmock.api.SchoolClass;
import ch.tbz.schoolmock.repository.UserFixtureRepository;

/**
 * Read-only REST endpoints that simulate the school systems BetterIntranet has no access to.
 */
@RestController
@RequestMapping("/mock/users/{username}")
public class MockUserController {

	public MockUserController(UserFixtureRepository repository) {
	}

	@GetMapping("/classes")
	public List<SchoolClass> classes(@PathVariable String username) {
		throw new UnsupportedOperationException("not implemented");
	}

	@GetMapping("/courses")
	public List<Course> courses(@PathVariable String username) {
		throw new UnsupportedOperationException("not implemented");
	}

	@GetMapping("/absences")
	public List<Absence> absences(@PathVariable String username) {
		throw new UnsupportedOperationException("not implemented");
	}

	@GetMapping("/grades")
	public List<Grade> grades(@PathVariable String username) {
		throw new UnsupportedOperationException("not implemented");
	}
}
