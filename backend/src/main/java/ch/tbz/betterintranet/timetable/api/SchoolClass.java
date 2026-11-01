package ch.tbz.betterintranet.timetable.api;

/**
 * A class as known by the timetable system.
 *
 * @param id         timetable resource id, e.g. 7471
 * @param shortName  e.g. "AP 24b"
 * @param longName   e.g. "Informatiker/innen Applikationsentwicklung"
 * @param department department short name, e.g. "IT"
 */
public record SchoolClass(long id, String shortName, String longName, String department) {
}
