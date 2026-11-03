package ch.tbz.betterintranet.core.module;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.tbz.betterintranet.core.config.BetterIntranetProperties.ModuleSettings;
import ch.tbz.betterintranet.core.gateway.QueryGateway;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ModuleRegistryTest {

    @Test
    @DisplayName("a module is enabled unless configuration switches it off")
    void enabledByDefault() {
        var registry = new ModuleRegistry(
                Map.of("grades", new ModuleSettings(false), "absences", new ModuleSettings(true)),
                List.of("grades", "absences", "timetable"));

        assertThat(registry.isEnabled("grades")).isFalse();
        assertThat(registry.isEnabled("absences")).isTrue();
        assertThat(registry.isEnabled("timetable")).isTrue();
        assertThat(registry.isEnabled("something-unknown")).isTrue();
    }

    @Test
    @DisplayName("core is never a module and can never be disabled")
    void coreCannotBeDisabled() {
        var registry = new ModuleRegistry(Map.of("core", new ModuleSettings(false)), List.of());

        assertThat(registry.isEnabled("core")).isTrue();
        assertThat(registry.describeAll()).extracting(ModuleDescriptor::name).doesNotContain("core");
    }

    @Test
    @DisplayName("describes configured and discovered modules, sorted by name, without duplicates")
    void describesAllModules() {
        var registry = new ModuleRegistry(
                Map.of("grades", new ModuleSettings(false)),
                List.of("timetable", "grades", "core"));

        assertThat(registry.describeAll()).containsExactly(
                new ModuleDescriptor("grades", false),
                new ModuleDescriptor("timetable", true));
    }

    @Test
    @DisplayName("derives the module name from the first package segment below the base package")
    void derivesModuleFromPackage() {
        assertThat(ModuleRegistry.moduleOf(QueryGateway.class)).isEqualTo("core");
        assertThat(ModuleRegistry.moduleOf(ch.tbz.betterintranet.testmodule.api.PingQuery.class)).isEqualTo("testmodule");
    }

    @Test
    @DisplayName("rejects classes outside the base package")
    void rejectsForeignClasses() {
        assertThatThrownBy(() -> ModuleRegistry.moduleOf(String.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("java.lang.String");
    }
}
