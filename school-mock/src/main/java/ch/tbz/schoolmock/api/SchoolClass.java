package ch.tbz.schoolmock.api;

/**
 * A school class ("Klasse") the user is a member of, as served by {@code GET /mock/users/{username}/classes}.
 *
 * @param id           class id, identical to the id used by the timetable system (e.g. {@code 7471})
 * @param shortName    short class name such as {@code AP 24b}
 * @param longName     profession the class is trained in
 * @param department   department of the school the class belongs to
 * @param classTeacher short code of the class teacher
 * @param schoolYear   school year such as {@code 2026/27}
 * @param role         role of the user in this class, e.g. {@code STUDENT}
 */
public record SchoolClass(
		int id,
		String shortName,
		String longName,
		String department,
		String classTeacher,
		String schoolYear,
		String role) {
}
