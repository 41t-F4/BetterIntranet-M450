package ch.tbz.betterintranet.core.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * All {@code betterintranet.*} settings: default user, module switches and upstream URLs.
 *
 * @param defaultUser username used when the {@code X-User} header is absent
 * @param modules     per-module settings keyed by module name, e.g. {@code grades}
 * @param timetable   settings for the external timetable system
 * @param school      settings for the school systems (mocked)
 */
@ConfigurationProperties(prefix = "betterintranet")
public record BetterIntranetProperties(String defaultUser,
                                       Map<String, ModuleSettings> modules,
                                       Timetable timetable,
                                       School school) {

    public BetterIntranetProperties {
        modules = modules == null ? Map.of() : Map.copyOf(modules);
    }

    /** Per-module switch. {@code enabled} defaults to {@code true} when omitted. */
    public record ModuleSettings(Boolean enabled) {

        public boolean isEnabled() {
            return enabled == null || enabled;
        }
    }

    public record Timetable(String baseUrl, String defaultClass) {
    }

    public record School(String baseUrl) {
    }
}
