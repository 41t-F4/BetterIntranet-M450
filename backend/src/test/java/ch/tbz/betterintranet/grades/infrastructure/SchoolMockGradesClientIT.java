package ch.tbz.betterintranet.grades.infrastructure;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import ch.tbz.betterintranet.grades.api.SchoolPart;
import ch.tbz.betterintranet.grades.application.RawGrade;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/** Talks to a WireMock stand-in for {@code school-mock}; no real network. */
@SpringBootTest
class SchoolMockGradesClientIT {

    private static final String GRADES_PATH = "/mock/users/nicola.wehrli/grades";

    @RegisterExtension
    static WireMockExtension school = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void schoolBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("betterintranet.school.base-url", school::baseUrl);
    }

    @Autowired
    private SchoolMockGradesClient client;

    @Test
    @DisplayName("the grades of a known user are parsed in upstream order with all fields")
    void shouldParseGradesOfKnownUser() throws IOException {
        school.stubFor(get(urlPathEqualTo(GRADES_PATH))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(fixture("grades/nicola-wehrli.json"))));

        List<RawGrade> grades = client.fetchGrades("nicola.wehrli");

        assertThat(grades).hasSize(12);
        assertThat(grades.getFirst()).isEqualTo(new RawGrade(1, SchoolPart.BMS, "Deutsch", "HS 2026", "Aufsatz",
                LocalDate.of(2026, 9, 15), 5.0, 1.0));
        assertThat(grades.get(5)).isEqualTo(new RawGrade(6, SchoolPart.TBZ, "Modul 450 Applikationen testen",
                "HS 2026", "LB1 Testkonzept", LocalDate.of(2026, 9, 30), 5.5, 1.0));
        assertThat(grades.get(8).weight()).isZero();
        assertThat(grades.getLast().schoolPart()).isEqualTo(SchoolPart.UEK);
        school.verify(getRequestedFor(urlPathEqualTo(GRADES_PATH)));
    }

    @Test
    @DisplayName("an empty array means the user simply has no grades")
    void shouldReturnEmptyListForEmptyArray() {
        school.stubFor(get(urlPathEqualTo(GRADES_PATH))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("[]")));

        assertThat(client.fetchGrades("nicola.wehrli")).isEmpty();
    }

    @Test
    @DisplayName("unknown fields in the upstream payload are ignored")
    void shouldIgnoreUnknownFields() {
        school.stubFor(get(urlPathEqualTo(GRADES_PATH))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("""
                                [{ "id": 7, "schoolPart": "TBZ", "subject": "Modul 450", "semester": "HS 2026",
                                   "title": "LB1", "date": "2026-09-30", "grade": 5.5, "weight": 1.0, "teacher": "XY" }]
                                """)));

        List<RawGrade> grades = client.fetchGrades("nicola.wehrli");

        assertThat(grades).singleElement().extracting(RawGrade::id).isEqualTo(7L);
    }

    @Test
    @DisplayName("404 from the school system becomes NotFoundException naming the user")
    void shouldMapNotFound() {
        school.stubFor(get(urlPathEqualTo("/mock/users/ghost/grades"))
                .willReturn(aResponse().withStatus(404).withHeader("Content-Type", "application/problem+json")
                        .withBody("{\"status\":404,\"detail\":\"User ghost not found\"}")));

        assertThatThrownBy(() -> client.fetchGrades("ghost"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User ghost not found");
    }

    @Test
    @DisplayName("500 from the school system becomes UpstreamUnavailableException for upstream 'school'")
    void shouldMapServerError() {
        school.stubFor(get(urlPathEqualTo(GRADES_PATH)).willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> client.fetchGrades("nicola.wehrli"))
                .isInstanceOf(UpstreamUnavailableException.class)
                .hasFieldOrPropertyWithValue("upstream", "school");
    }

    @Test
    @DisplayName("a malformed payload becomes UpstreamUnavailableException as well")
    void shouldMapMalformedPayload() {
        school.stubFor(get(urlPathEqualTo(GRADES_PATH))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("{\"not\":\"an array\"")));

        assertThatThrownBy(() -> client.fetchGrades("nicola.wehrli"))
                .isInstanceOf(UpstreamUnavailableException.class)
                .hasFieldOrPropertyWithValue("upstream", "school");
    }

    private static String fixture(String path) throws IOException {
        return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
    }
}
