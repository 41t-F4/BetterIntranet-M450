package ch.tbz.betterintranet.courses.api;

/**
 * A course the user attends at the TBZ, as reported by the school system.
 *
 * @param id             course id, e.g. "450-HS26"
 * @param code           code shown to the user, e.g. "450" or "ENG"
 * @param name           e.g. "Applikationen testen"
 * @param teacher        teacher short name, e.g. "VAJ"
 * @param semester       e.g. "HS 2026"
 * @param lessonsPerWeek number of lessons per week
 * @param room           e.g. "AU-060"
 */
public record Course(String id,
                     String code,
                     String name,
                     String teacher,
                     String semester,
                     int lessonsPerWeek,
                     String room) {
}
