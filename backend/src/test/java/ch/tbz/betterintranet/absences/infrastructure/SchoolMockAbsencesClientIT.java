package ch.tbz.betterintranet.absences.infrastructure;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.tbz.betterintranet.absences.api.Absence;
import ch.tbz.betterintranet.absences.api.AbsenceStatus;
import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

/** Runs the real HTTP client against a WireMock stand-in for {@code school-mock}; no network access. */
@SpringBootTest
class SchoolMockAbsencesClientIT {

    private static final String PATH = "/mock/users/nicola.wehrli/absences";
    private static final WireMockServer SCHOOL = new WireMockServer(options().dynamicPort());

    static {
        SCHOOL.start();
    }

    @DynamicPropertySource
    static void schoolBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("betterintranet.school.base-url", SCHOOL::baseUrl);
    }

    @AfterAll
    static void stopSchool() {
        SCHOOL.stop();
    }

    @Autowired
    private SchoolMockAbsencesClient client;

    @BeforeEach
    void resetStubs() {
        SCHOOL.resetAll();
    }

    @Test
    @DisplayName("the JSON of the school system is mapped to absences")
    void mapsAbsences() throws IOException {
        SCHOOL.stubFor(get(urlEqualTo(PATH)).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(fixture("absences/nicola-wehrli.json"))));

        List<Absence> absences = client.fetchAbsences("nicola.wehrli");

        assertThat(absences).hasSize(7);
        assertThat(absences.getFirst()).isEqualTo(new Absence(1, LocalDate.of(2026, 10, 12), LocalTime.of(8, 10),
                LocalTime.of(9, 45), "Englisch", "MID", AbsenceStatus.OPEN, null,
                LocalDateTime.of(2026, 10, 12, 10, 0)));
        assertThat(absences).extracting(Absence::status)
                .contains(AbsenceStatus.OPEN, AbsenceStatus.EXCUSED, AbsenceStatus.UNEXCUSED);
        assertThat(absences).filteredOn(a -> a.id() == 3).singleElement()
                .extracting(Absence::reason).isEqualTo("Arztbesuch");
    }

    @Test
    @DisplayName("an unknown user (404 problem detail) becomes NotFoundException")
    void unknownUser() {
        SCHOOL.stubFor(get(urlEqualTo("/mock/users/unknown/absences")).willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/problem+json")
                .withBody("{\"type\":\"about:blank\",\"title\":\"Not Found\",\"status\":404,"
                        + "\"detail\":\"User 'unknown' not found\"}")));

        assertThatThrownBy(() -> client.fetchAbsences("unknown"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    @DisplayName("a server error of the school system becomes UpstreamUnavailableException")
    void serverError() {
        SCHOOL.stubFor(get(urlEqualTo(PATH)).willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> client.fetchAbsences("nicola.wehrli"))
                .isInstanceOf(UpstreamUnavailableException.class)
                .hasMessageContaining("500")
                .extracting("upstream").isEqualTo("school");
    }

    @Test
    @DisplayName("a broken connection becomes UpstreamUnavailableException")
    void connectionReset() {
        SCHOOL.stubFor(get(urlEqualTo(PATH)).willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

        assertThatThrownBy(() -> client.fetchAbsences("nicola.wehrli"))
                .isInstanceOf(UpstreamUnavailableException.class)
                .extracting("upstream").isEqualTo("school");
    }

    @Test
    @DisplayName("a school system that is not running becomes UpstreamUnavailableException")
    void connectionRefused() throws IOException {
        var offline = new SchoolMockAbsencesClient(
                RestClient.builder().baseUrl("http://localhost:" + unusedPort()).build());

        assertThatThrownBy(() -> offline.fetchAbsences("nicola.wehrli"))
                .isInstanceOf(UpstreamUnavailableException.class)
                .extracting("upstream").isEqualTo("school");
    }

    @Test
    @DisplayName("a body that does not match the contract becomes UpstreamUnavailableException")
    void malformedBody() {
        SCHOOL.stubFor(get(urlEqualTo(PATH)).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("[{\"id\":1,\"date\":\"2026-10-12\",\"status\":\"MAYBE\"}]")));

        assertThatThrownBy(() -> client.fetchAbsences("nicola.wehrli"))
                .isInstanceOf(UpstreamUnavailableException.class);
    }

    private static String fixture(String path) throws IOException {
        return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
    }

    private static int unusedPort() throws IOException {
        try (var socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
