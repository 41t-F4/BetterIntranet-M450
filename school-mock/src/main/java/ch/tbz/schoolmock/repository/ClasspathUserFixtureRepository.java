package ch.tbz.schoolmock.repository;

import java.util.List;

import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import ch.tbz.schoolmock.api.Absence;
import ch.tbz.schoolmock.api.Course;
import ch.tbz.schoolmock.api.Grade;
import ch.tbz.schoolmock.api.SchoolClass;
import tools.jackson.databind.ObjectMapper;

/**
 * {@link UserFixtureRepository} backed by JSON files on the classpath under
 * {@code data/users/<username>/{classes,courses,absences,grades}.json}.
 */
@Repository
public class ClasspathUserFixtureRepository implements UserFixtureRepository {

	public ClasspathUserFixtureRepository(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
	}

	@Override
	public List<SchoolClass> findClasses(String username) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Course> findCourses(String username) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Absence> findAbsences(String username) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Grade> findGrades(String username) {
		throw new UnsupportedOperationException("not implemented");
	}
}
