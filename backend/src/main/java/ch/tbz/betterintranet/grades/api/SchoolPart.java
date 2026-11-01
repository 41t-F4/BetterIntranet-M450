package ch.tbz.betterintranet.grades.api;

/** The part of the school a grade belongs to: Berufsmaturitätsschule, Technische Berufsschule or überbetriebliche Kurse. */
public enum SchoolPart {
    BMS("BMS"),
    TBZ("TBZ"),
    UEK("ÜK");

    private final String label;

    SchoolPart(String label) {
        this.label = label;
    }

    /** Label shown in the UI, e.g. {@code ÜK} for {@link #UEK}. */
    public String label() {
        return label;
    }
}
