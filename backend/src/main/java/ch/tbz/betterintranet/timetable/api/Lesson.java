package ch.tbz.betterintranet.timetable.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * One block in the timetable grid.
 *
 * @param date       day of the lesson
 * @param start      start time
 * @param end        end time
 * @param subject    subject short name, e.g. "ENG"
 * @param subjectName subject long name, e.g. "Englisch"
 * @param teachers   short names of all teachers, e.g. ["MID"]; two or more for team teaching, empty if unknown
 * @param rooms      all rooms, e.g. ["AU-512"]; empty if unknown
 * @param status     normalised status
 * @param info       free text from the upstream, may be null
 */
public record Lesson(LocalDate date, LocalTime start, LocalTime end, String subject, String subjectName,
                     List<String> teachers, List<String> rooms, LessonStatus status, String info) {

    public Lesson {
        teachers = teachers == null ? List.of() : List.copyOf(teachers);
        rooms = rooms == null ? List.of() : List.copyOf(rooms);
    }
}
