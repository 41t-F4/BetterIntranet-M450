package ch.tbz.betterintranet.grades.domain;

import java.util.Comparator;

/** Orders semester labels such as {@code HS 2026} and {@code FS 2026}. */
public final class SemesterOrder {

    private SemesterOrder() {
    }

    public static Comparator<String> newestFirst() {
        return (left, right) -> {
            throw new UnsupportedOperationException("not implemented");
        };
    }
}
