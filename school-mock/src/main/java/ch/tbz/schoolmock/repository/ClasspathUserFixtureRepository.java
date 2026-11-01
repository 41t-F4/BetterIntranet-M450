package ch.tbz.schoolmock.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import ch.tbz.schoolmock.api.Absence;
import ch.tbz.schoolmock.api.Course;
import ch.tbz.schoolmock.api.Grade;
import ch.tbz.schoolmock.api.SchoolClass;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * {@link UserFixtureRepository} backed by JSON files on the classpath under
 * {@code data/users/<username>/{classes,courses,absences,grades}.json}.
 *
 * <p>The four files of a user are parsed on first access and then kept in memory for the lifetime
 * of the application; fixtures are static, so there is no invalidation. Usernames are restricted to
 * plain identifiers so that a path such as {@code ../application} can never resolve to a resource
 * outside the fixture folder.
 */
@Repository
public class ClasspathUserFixtureRepository implements UserFixtureRepository {

	private static final String FIXTURE_ROOT = "classpath:data/users/";

	private static final Pattern USERNAME = Pattern.compile("[a-z0-9][a-z0-9._-]{0,63}");

	private final ObjectMapper objectMapper;

	private final ResourceLoader resourceLoader;

	private final ConcurrentMap<String, UserFixtures> cache = new ConcurrentHashMap<>();

	public ClasspathUserFixtureRepository(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
		this.objectMapper = objectMapper;
		this.resourceLoader = resourceLoader;
	}

	@Override
	public List<SchoolClass> findClasses(String username) {
		return fixturesOf(username).classes();
	}

	@Override
	public List<Course> findCourses(String username) {
		return fixturesOf(username).courses();
	}

	@Override
	public List<Absence> findAbsences(String username) {
		return fixturesOf(username).absences();
	}

	@Override
	public List<Grade> findGrades(String username) {
		return fixturesOf(username).grades();
	}

	private UserFixtures fixturesOf(String username) {
		if (username == null || !USERNAME.matcher(username).matches()) {
			throw new UserNotFoundException(username);
		}
		return cache.computeIfAbsent(username, this::load);
	}

	private UserFixtures load(String username) {
		return new UserFixtures(
				read(username, "classes", SchoolClass.class),
				read(username, "courses", Course.class),
				read(username, "absences", Absence.class),
				read(username, "grades", Grade.class));
	}

	private <T> List<T> read(String username, String fixtureName, Class<T> elementType) {
		String fileName = fixtureName + ".json";
		Resource resource = resourceLoader.getResource(FIXTURE_ROOT + username + "/" + fileName);
		if (!resource.exists()) {
			throw new UserNotFoundException(username);
		}
		try (InputStream in = resource.getInputStream()) {
			List<T> items = objectMapper.readerForListOf(elementType).readValue(in);
			return List.copyOf(items);
		}
		catch (IOException | JacksonException ex) {
			throw new FixtureLoadException(
					"Cannot read fixture " + fileName + " of user '" + username + "'", ex);
		}
	}

	/** All fixture data of one user, parsed once. */
	private record UserFixtures(
			List<SchoolClass> classes,
			List<Course> courses,
			List<Absence> absences,
			List<Grade> grades) {
	}
}
