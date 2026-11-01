package ch.tbz.betterintranet.absences.api;

/** Status of an absence as reported by the school system, with the German label shown in the UI. */
public enum AbsenceStatus {
    OPEN("Offen"),
    EXCUSED("Entschuldigt"),
    UNEXCUSED("Unentschuldigt");

    private final String label;

    AbsenceStatus(String label) {
        this.label = label;
    }

    public String label() {
        throw new UnsupportedOperationException("not implemented");
    }
}
