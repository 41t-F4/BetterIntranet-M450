package ch.tbz.schoolmock.api;

/**
 * A course ("Kurs") of the TBZ Berufsschule the user attends, either an ICT module or a Berufsschule
 * subject such as Englisch or Allgemeinbildung, as served by {@code GET /mock/users/{username}/courses}.
 *
 * @param id             stable identifier of the course, e.g. {@code 450-HS26}
 * @param code           short code shown to the user, the module number such as {@code 450} or a
 *                       subject code such as {@code ENG}
 * @param name           course name without the code, e.g. {@code Applikationen testen}
 * @param teacher        short code of the teacher
 * @param semester       semester the course runs in, e.g. {@code HS 2026}
 * @param lessonsPerWeek number of lessons per week
 * @param room           room the course usually takes place in
 */
public record Course(
		String id,
		String code,
		String name,
		String teacher,
		String semester,
		int lessonsPerWeek,
		String room) {
}
