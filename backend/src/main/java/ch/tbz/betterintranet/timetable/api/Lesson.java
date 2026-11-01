package ch.tbz.betterintranet.timetable.api;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * One block in the timetable grid.
 *
 * @param date       day of the lesson
 * @param start      start time
 * @param end        end time
 * @param subject    subject short name, e.g. "ENG"
 * @param subjectName subject long name, e.g. "Englisch"
 * @param teacher    teacher short name, e.g. "MID"
 * @param room       room, e.g. "AU-512", may be null
 * @param status     normalised status
 * @param info       free text from the upstream, may be null
 */
public record Lesson(LocalDate date, LocalTime start, LocalTime end, String subject, String subjectName,
                     String teacher, String room, LessonStatus status, String info) {
}
