package ch.tbz.betterintranet.classes.infrastructure;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.tbz.betterintranet.classes.api.SchoolClassMembership;
import ch.tbz.betterintranet.classes.application.ClassesClient;
import ch.tbz.betterintranet.core.error.NotFoundException;
import ch.tbz.betterintranet.core.error.UpstreamUnavailableException;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/** Talks to a WireMock stand-in for school-mock; no real network access. */
@SpringBootTest
class SchoolMockClassesClientIT {

    @RegisterExtension
    static final WireMockExtension schoolMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void schoolBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("betterintranet.school.base-url", schoolMock::baseUrl);
    }

    @Autowired
    private ClassesClient client;

    @Test
    @DisplayName("maps the classes of a known user from the school-mock JSON")
    void mapsClassesOfKnownUser() throws IOException {
        schoolMock.stubFor(get(urlEqualTo("/mock/users/nicola.wehrli/classes"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(fixture("classes/nicola-wehrli.json"))));

        List<SchoolClassMembership> classes = client.classesOf("nicola.wehrli");

        assertThat(classes).containsExactly(
                new SchoolClassMembership(7471, "AP 24b", "Informatiker/innen Applikationsentwicklung", "IT", "MID",
                        "2026/27", "STUDENT"),
                new SchoolClassMembership(6890, "AP 24b", "Informatiker/innen Applikationsentwicklung", "IT", "MID",
                        "2025/26", "STUDENT"));
        schoolMock.verify(getRequestedFor(urlEqualTo("/mock/users/nicola.wehrli/classes")));
    }

    @Test
    @DisplayName("returns an empty list when the school system answers with an empty array")
    void returnsEmptyListForEmptyArray() {
        schoolMock.stubFor(get(urlEqualTo("/mock/users/max.muster/classes"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        assertThat(client.classesOf("max.muster")).isEmpty();
    }

    @Test
    @DisplayName("translates 404 into NotFoundException")
    void translates404IntoNotFound() {
        schoolMock.stubFor(get(urlEqualTo("/mock/users/unknown/classes"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/problem+json")
                        .withBody("{\"status\":404,\"title\":\"Not Found\",\"detail\":\"Unknown user 'unknown'\"}")));

        assertThatThrownBy(() -> client.classesOf("unknown"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    @DisplayName("translates a server error into UpstreamUnavailableException naming the school upstream")
    void translates500IntoUpstreamUnavailable() {
        schoolMock.stubFor(get(urlEqualTo("/mock/users/nicola.wehrli/classes"))
                .willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> client.classesOf("nicola.wehrli"))
                .isInstanceOf(UpstreamUnavailableException.class)
                .extracting("upstream").isEqualTo("school");
    }

    @Test
    @DisplayName("translates a connection failure into UpstreamUnavailableException")
    void translatesConnectionFailureIntoUpstreamUnavailable() {
        schoolMock.stubFor(get(urlEqualTo("/mock/users/nicola.wehrli/classes"))
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

        assertThatThrownBy(() -> client.classesOf("nicola.wehrli"))
                .isInstanceOf(UpstreamUnavailableException.class)
                .extracting("upstream").isEqualTo("school");
    }

    private static String fixture(String path) throws IOException {
        return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
    }
}
