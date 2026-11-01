package ch.tbz.betterintranet.timetable.application;

import ch.tbz.betterintranet.core.gateway.QueryHandler;
import ch.tbz.betterintranet.timetable.api.GetHolidaysQuery;
import ch.tbz.betterintranet.timetable.api.Holiday;
import java.util.List;
import java.util.function.Supplier;

/**
 * Test double for the timetable module's holidays handler. It lives in the {@code timetable.application}
 * package so the {@link ch.tbz.betterintranet.core.gateway.HandlerRegistry} attributes it to the
 * {@code timetable} module and the module switch applies. It is not a Spring component: tests that need
 * it register it explicitly, so it never clashes with the real handler.
 */
public class FakeHolidaysQueryHandler implements QueryHandler<GetHolidaysQuery, List<Holiday>> {

    private Supplier<List<Holiday>> answer = List::of;

    public void answerWith(List<Holiday> holidays) {
        this.answer = () -> holidays;
    }

    public void failWith(RuntimeException failure) {
        this.answer = () -> {
            throw failure;
        };
    }

    @Override
    public List<Holiday> handle(GetHolidaysQuery query) {
        return answer.get();
    }
}
