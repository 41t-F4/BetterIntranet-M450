package ch.tbz.betterintranet.grades.domain;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Orders semester labels as delivered by the school system, newest first.
 *
 * <p>Recognised labels are {@code HS yyyy} (Herbstsemester, autumn) and {@code FS yyyy} (Frühlingssemester,
 * spring). A higher year comes first; within the same year {@code HS} comes before {@code FS} because the
 * autumn semester is the later one. Labels that do not follow this pattern (including {@code null}) come
 * after all recognised ones, sorted alphabetically, {@code null} last.
 */
public final class SemesterOrder {

    private static final Pattern SEMESTER = Pattern.compile("(HS|FS)\\s+(\\d{4})");

    private static final Comparator<String> NEWEST_FIRST = Comparator
            .comparing(SemesterOrder::recency, Comparator.nullsLast(Comparator.<Integer>reverseOrder()))
            .thenComparing(Comparator.nullsLast(Comparator.<String>naturalOrder()));

    private SemesterOrder() {
    }

    /** Comparator that sorts semester labels newest first, unparseable labels last and alphabetically. */
    public static Comparator<String> newestFirst() {
        return NEWEST_FIRST;
    }

    /** Monotonic rank of a semester ({@code year * 2}, plus one for autumn), {@code null} when unparseable. */
    private static Integer recency(String label) {
        if (label == null) {
            return null;
        }
        Matcher matcher = SEMESTER.matcher(label.strip());
        if (!matcher.matches()) {
            return null;
        }
        int year = Integer.parseInt(matcher.group(2));
        int autumn = "HS".equals(matcher.group(1)) ? 1 : 0;
        return year * 2 + autumn;
    }
}
