package ch.tbz.betterintranet.courses.api;

/**
 * A course the user attends, as reported by the school system.
 *
 * @param id             course id, e.g. "M450" or "BM-D"
 * @param code           code shown to the user, e.g. "M450" or "D"
 * @param name           e.g. "Applikationen testen"
 * @param teacher        teacher short name, e.g. "STU"
 * @param semester       e.g. "HS 2026"
 * @param lessonsPerWeek number of lessons per week
 * @param room           e.g. "AU-512"
 * @param schoolPart     part of the school giving the course: {@code BMS}, {@code TBZ} or {@code UEK}
 */
public record Course(String id,
                     String code,
                     String name,
                     String teacher,
                     String semester,
                     int lessonsPerWeek,
                     String room,
                     String schoolPart) {
}
