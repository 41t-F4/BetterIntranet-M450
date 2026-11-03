package ch.tbz.betterintranet.timetable;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/** Real responses recorded from stundenplan.geekz.ch, see {@code docs/fixtures}. */
public final class TimetableFixtures {

    public static final String ENTRIES_AP24B = "timetable/geekz-entries-ap24b-2026-11-02.json";
    public static final String CLASSES = "timetable/geekz-filter-classes.json";
    public static final String HOLIDAYS = "timetable/geekz-holidays.json";

    private TimetableFixtures() {
    }

    public static String read(String resource) {
        try (InputStream in = TimetableFixtures.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalArgumentException("fixture not found: " + resource);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
