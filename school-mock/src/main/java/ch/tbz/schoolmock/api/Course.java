package ch.tbz.schoolmock.api;

/**
 * A course ("Kurs") the user attends in the current semester, as served by
 * {@code GET /mock/users/{username}/courses}.
 *
 * @param id             stable identifier of the course, e.g. {@code M450} or {@code BM-D}
 * @param code           short code shown to the user, e.g. {@code M450} or {@code ÜK 335}
 * @param name           course name without the code, e.g. {@code Applikationen testen}
 * @param teacher        short code of the teacher or course instructor
 * @param semester       semester the course runs in, e.g. {@code HS 2026}
 * @param lessonsPerWeek number of lessons per week
 * @param room           room the course usually takes place in
 * @param schoolPart     part of the school offering the course
 */
public record Course(
		String id,
		String code,
		String name,
		String teacher,
		String semester,
		int lessonsPerWeek,
		String room,
		SchoolPart schoolPart) {
}
