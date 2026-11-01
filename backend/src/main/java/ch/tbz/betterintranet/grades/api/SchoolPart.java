package ch.tbz.betterintranet.grades.api;

/** The part of the school a grade belongs to: Berufsmaturitätsschule, Technische Berufsschule or überbetriebliche Kurse. */
public enum SchoolPart {
    BMS,
    TBZ,
    UEK;

    /** Label shown in the UI, e.g. {@code ÜK} for {@link #UEK}. */
    public String label() {
        throw new UnsupportedOperationException("not implemented");
    }
}
