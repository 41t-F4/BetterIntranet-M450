package ch.tbz.betterintranet.core.module;

import ch.tbz.betterintranet.core.config.BetterIntranetProperties.ModuleSettings;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Knows which feature modules exist and whether they are enabled. A module is identified by the first
 * package segment below {@code ch.tbz.betterintranet}, e.g. {@code grades} for
 * {@code ch.tbz.betterintranet.grades.application.GetGradesHandler}. The {@code core} package is not a
 * module and can never be disabled.
 */
public class ModuleRegistry {

    public static final String BASE_PACKAGE = "ch.tbz.betterintranet";
    public static final String CORE = "core";

    /**
     * @param settings          per-module switches from configuration
     * @param discoveredModules module names found at startup (e.g. from registered handlers)
     */
    public ModuleRegistry(Map<String, ModuleSettings> settings, Collection<String> discoveredModules) {
        throw new UnsupportedOperationException("not implemented");
    }

    public boolean isEnabled(String moduleName) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** All known modules (configured or discovered), sorted by name. */
    public List<ModuleDescriptor> describeAll() {
        throw new UnsupportedOperationException("not implemented");
    }

    /** Derives the module name from a class' package. */
    public static String moduleOf(Class<?> type) {
        throw new UnsupportedOperationException("not implemented");
    }
}
