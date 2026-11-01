package ch.tbz.betterintranet.dashboard.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import ch.tbz.betterintranet.dashboard.api.EventKind;
import ch.tbz.betterintranet.dashboard.api.Link;
import ch.tbz.betterintranet.dashboard.api.NewsItem;
import ch.tbz.betterintranet.dashboard.api.PreventionNotice;
import ch.tbz.betterintranet.dashboard.api.SchoolEvent;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

class ClasspathDashboardContentRepositoryTest {

    private final ObjectMapper mapper = JsonMapper.builder().build();
    private final ClasspathDashboardContentRepository repository = new ClasspathDashboardContentRepository(mapper);

    @Test
    @DisplayName("content.json ships three news items with id, title, text and publication date")
    void readsNews() {
        assertThat(repository.news()).hasSize(3)
                .extracting(NewsItem::id).containsExactlyInAnyOrder(1L, 2L, 3L);
        assertThat(repository.news()).allSatisfy(item -> {
            assertThat(item.title()).isNotBlank();
            assertThat(item.text()).isNotBlank();
            assertThat(item.publishedAt()).isNotNull();
        });
        assertThat(repository.news()).filteredOn(item -> item.id() == 3).singleElement()
                .satisfies(item -> {
                    assertThat(item.title()).isEqualTo("Tag der offenen Tür 2026");
                    assertThat(item.publishedAt()).isEqualTo(LocalDate.of(2026, 10, 28));
                });
    }

    @Test
    @DisplayName("content.json ships four school events of kind EVENT between November 2026 and March 2027")
    void readsEvents() {
        assertThat(repository.events()).hasSize(4)
                .extracting(SchoolEvent::kind).containsOnly(EventKind.EVENT);
        assertThat(repository.events()).extracting(SchoolEvent::title)
                .contains("Elternabend Abteilung IT", "Prüfungswoche", "Tag der offenen Tür", "Schulschluss 1. Semester");
        assertThat(repository.events()).allSatisfy(event -> {
            assertThat(event.startDate()).isAfterOrEqualTo(LocalDate.of(2026, 11, 1));
            assertThat(event.endDate()).isAfterOrEqualTo(event.startDate()).isBeforeOrEqualTo(LocalDate.of(2027, 3, 31));
        });
    }

    @Test
    @DisplayName("content.json ships the 147.ch prevention notice")
    void readsPrevention() {
        assertThat(repository.prevention()).isEqualTo(new PreventionNotice("Prävention",
                "147.ch unterstützt junge Menschen bei Sorgen & Problemen.", "www.147.ch", "https://www.147.ch"));
    }

    @Test
    @DisplayName("Meine Links are internal routes to courses, absences and grades")
    void readsMyLinks() {
        assertThat(repository.myLinks()).containsExactly(
                new Link("Meine Kurse", "/klassen-kurse", false),
                new Link("Absenzen: Einträge", "/absenzen", false),
                new Link("Noten", "/noten", false));
    }

    @Test
    @DisplayName("Quick Links are the eight external services of the old intranet")
    void readsQuickLinks() {
        assertThat(repository.quickLinks()).extracting(Link::label).containsExactly(
                "Service Desk Pro", "Absenzen", "Dateiablage", "Hilfe zum Intranet", "Mediothek", "OrgWeb",
                "Tagesagenda Abteilung IT", "Microsoft 365 Portal");
        assertThat(repository.quickLinks()).allSatisfy(link -> {
            assertThat(link.external()).isTrue();
            assertThat(link.url()).startsWith("https://");
        });
    }

    @Test
    @DisplayName("fails at startup when content.json is missing")
    void failsFastWhenFileIsMissing() {
        assertThatIllegalStateException()
                .isThrownBy(() -> new ClasspathDashboardContentRepository(mapper, "dashboard/does-not-exist.json"))
                .withMessageContaining("dashboard/does-not-exist.json");
    }

    @Test
    @DisplayName("fails at startup when content.json cannot be parsed")
    void failsFastWhenFileIsMalformed() {
        assertThatIllegalStateException()
                .isThrownBy(() -> new ClasspathDashboardContentRepository(mapper, "dashboard/malformed-content.json"))
                .withMessageContaining("dashboard/malformed-content.json");
    }
}
