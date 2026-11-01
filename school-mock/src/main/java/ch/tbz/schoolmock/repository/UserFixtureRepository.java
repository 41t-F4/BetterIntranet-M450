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
 * when no fixtures exist for the given username.
 */
public interface UserFixtureRepository {

	/**
	 * @param username login name of the user, e.g. {@code nicola.wehrli}
	 * @return the classes the user is a member of, never {@code null}
	 * @throws UserNotFoundException when no fixtures exist for the user
	 */
	List<SchoolClass> findClasses(String username);

	/**
	 * @param username login name of the user
	 * @return the courses the user attends, never {@code null}
	 * @throws UserNotFoundException when no fixtures exist for the user
	 */
	List<Course> findCourses(String username);

	/**
	 * @param username login name of the user
	 * @return the absence entries of the user, never {@code null}
	 * @throws UserNotFoundException when no fixtures exist for the user
	 */
	List<Absence> findAbsences(String username);

	/**
	 * @param username login name of the user
	 * @return the grades of the user as a flat list, never {@code null}
	 * @throws UserNotFoundException when no fixtures exist for the user
	 */
	List<Grade> findGrades(String username);
}
