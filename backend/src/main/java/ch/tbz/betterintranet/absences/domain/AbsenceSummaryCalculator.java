package ch.tbz.betterintranet.absences.domain;

import ch.tbz.betterintranet.absences.api.Absence;
import ch.tbz.betterintranet.absences.api.AbsenceSummary;
import java.time.Duration;
import java.util.List;

/**
 * Derives the {@link AbsenceSummary} (counts per status, missed lessons) from a list of absences.
 *
 * <p>Missed lessons are counted per entry: the duration is rounded up to whole 45-minute lessons, tolerating the
 * 5-minute break between two consecutive lessons. A double lesson 08:10-09:45 therefore counts as 2 lessons.
 * Longer breaks in the school's grid are not modelled; an entry with no (or negative) duration counts as 0.
 */
public class AbsenceSummaryCalculator {

    static final int LESSON_MINUTES = 45;
    static final int BREAK_MINUTES = 5;

    public AbsenceSummary summarize(List<Absence> absences) {
        int open = 0;
        int excused = 0;
        int unexcused = 0;
        int lessons = 0;
        for (Absence absence : absences) {
            switch (absence.status()) {
                case OPEN -> open++;
                case EXCUSED -> excused++;
                case UNEXCUSED -> unexcused++;
            }
            lessons += lessonsOf(absence);
        }
        return new AbsenceSummary(absences.size(), open, excused, unexcused, lessons);
    }

    private static int lessonsOf(Absence absence) {
        long minutes = Duration.between(absence.lessonStart(), absence.lessonEnd()).toMinutes();
        if (minutes <= 0) {
            return 0;
        }
        return (int) Math.ceilDiv(minutes + BREAK_MINUTES, LESSON_MINUTES + BREAK_MINUTES);
    }
}
