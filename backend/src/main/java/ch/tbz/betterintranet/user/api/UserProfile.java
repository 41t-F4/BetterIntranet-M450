package ch.tbz.betterintranet.user.api;

/**
 * The signed-in user as shown in the header and used by the other modules.
 *
 * @param username    login name, e.g. {@code nicola.wehrli}
 * @param displayName full name for the UI, e.g. {@code Nicola Wehrli}
 * @param firstName   first name
 * @param lastName    last name
 * @param email       school e-mail address
 * @param className   short name of the user's class as known by the timetable, e.g. {@code AP 24b}
 * @param classId     timetable resource id of that class, e.g. {@code 7471}
 * @param role        role at school, e.g. {@code STUDENT}
 */
public record UserProfile(String username,
                          String displayName,
                          String firstName,
                          String lastName,
                          String email,
                          String className,
                          long classId,
                          String role) {
}
