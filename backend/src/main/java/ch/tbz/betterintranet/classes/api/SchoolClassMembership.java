package ch.tbz.betterintranet.classes.api;

/**
 * A class the user belongs to, as reported by the school system.
 *
 * @param id           class id, e.g. 7471
 * @param shortName    e.g. "AP 24b"
 * @param longName     e.g. "Informatiker/innen Applikationsentwicklung"
 * @param department   department short name, e.g. "IT"
 * @param classTeacher class teacher short name, e.g. "MID"
 * @param schoolYear   e.g. "2026/27"
 * @param role         the user's role in the class, e.g. "STUDENT"
 */
public record SchoolClassMembership(long id,
                                    String shortName,
                                    String longName,
                                    String department,
                                    String classTeacher,
                                    String schoolYear,
                                    String role) {
}
