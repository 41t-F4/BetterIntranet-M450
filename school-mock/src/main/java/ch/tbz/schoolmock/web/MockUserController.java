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
 * Read-only REST endpoints that simulate the school systems BetterIntranet has no access to
 * (class administration, course enrolment, absences and grades).
 *
 * <p>All resources are served per user below {@code /mock/users/{username}} straight from the
 * fixture repository. The controller contains no logic of its own so that the main backend can
 * rely on a stable, predictable contract.
 */
@RestController
@RequestMapping("/mock/users/{username}")
public class MockUserController {

	private final UserFixtureRepository repository;

	public MockUserController(UserFixtureRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/classes")
	public List<SchoolClass> classes(@PathVariable String username) {
		return repository.findClasses(username);
	}

	@GetMapping("/courses")
	public List<Course> courses(@PathVariable String username) {
		return repository.findCourses(username);
	}

	@GetMapping("/absences")
	public List<Absence> absences(@PathVariable String username) {
		return repository.findAbsences(username);
	}

	@GetMapping("/grades")
	public List<Grade> grades(@PathVariable String username) {
		return repository.findGrades(username);
	}
}
