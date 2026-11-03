package ch.tbz.betterintranet.timetable.api;

/** Normalised lesson status; anything the upstream reports that is not regular ends up as CHANGED or CANCELLED. */
public enum LessonStatus {
    REGULAR,
    CANCELLED,
    CHANGED
}
