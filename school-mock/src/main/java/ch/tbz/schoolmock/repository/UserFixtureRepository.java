package ch.tbz.schoolmock.repository;

import java.util.List;

import ch.tbz.schoolmock.api.Absence;
import ch.tbz.schoolmock.api.Course;
import ch.tbz.schoolmock.api.Grade;
import ch.tbz.schoolmock.api.SchoolClass;

/**
 * Read access to the per-user fixture data this mock serves instead of the real school systems.
 *
 * <p>Every method answers for exactly one user. Implementations throw {@link UserNotFoundException}
 * when no fixtures exist for the given username and {@link FixtureLoadException} when a fixture
 * exists but cannot be read.
 */
public interface UserFixtureRepository {

	/**
	 * Returns the classes the user is a member of.
	 *
	 * @param username login name of the user, e.g. {@code nicola.wehrli}
	 * @return the classes, never {@code null}
	 * @throws UserNotFoundException when no fixtures exist for the user
	 */
	List<SchoolClass> findClasses(String username);

	/**
	 * Returns the courses the user attends in the current semester.
	 *
	 * @param username login name of the user
	 * @return the courses, never {@code null}
	 * @throws UserNotFoundException when no fixtures exist for the user
	 */
	List<Course> findCourses(String username);

	/**
	 * Returns the absence entries recorded for the user.
	 *
	 * @param username login name of the user
	 * @return the absence entries, never {@code null}
	 * @throws UserNotFoundException when no fixtures exist for the user
	 */
	List<Absence> findAbsences(String username);

	/**
	 * Returns all grades of the user as a flat list across school parts and semesters.
	 *
	 * @param username login name of the user
	 * @return the grades, never {@code null}
	 * @throws UserNotFoundException when no fixtures exist for the user
	 */
	List<Grade> findGrades(String username);
}
