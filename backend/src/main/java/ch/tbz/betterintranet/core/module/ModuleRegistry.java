package ch.tbz.betterintranet.core.module;

import ch.tbz.betterintranet.core.config.BetterIntranetProperties.ModuleSettings;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Knows which feature modules exist and whether they are enabled. A module is identified by the first
 * package segment below {@code ch.tbz.betterintranet}, e.g. {@code grades} for
 * {@code ch.tbz.betterintranet.grades.application.GetGradesHandler}. The {@code core} package is not a
 * module and can never be disabled.
 */
public class ModuleRegistry {

    public static final String BASE_PACKAGE = "ch.tbz.betterintranet";
    public static final String CORE = "core";

    private final Map<String, ModuleSettings> settings;
    private final List<String> knownModules;

    /**
     * @param settings          per-module switches from configuration
     * @param discoveredModules module names found at startup (e.g. from registered handlers)
     */
    public ModuleRegistry(Map<String, ModuleSettings> settings, Collection<String> discoveredModules) {
        this.settings = Map.copyOf(settings);
        var names = new TreeSet<>(settings.keySet());
        names.addAll(discoveredModules);
        names.remove(CORE);
        this.knownModules = List.copyOf(names);
    }

    public boolean isEnabled(String moduleName) {
        if (CORE.equals(moduleName)) {
            return true;
        }
        ModuleSettings moduleSettings = settings.get(moduleName);
        return moduleSettings == null || moduleSettings.isEnabled();
    }

    /** All known modules (configured or discovered), sorted by name. */
    public List<ModuleDescriptor> describeAll() {
        return knownModules.stream()
                .map(name -> new ModuleDescriptor(name, isEnabled(name)))
                .toList();
    }

    /** Derives the module name from a class' package. */
    public static String moduleOf(Class<?> type) {
        String packageName = type.getPackageName();
        String prefix = BASE_PACKAGE + ".";
        if (!packageName.startsWith(prefix)) {
            throw new IllegalArgumentException(type.getName() + " is not part of " + BASE_PACKAGE);
        }
        String remainder = packageName.substring(prefix.length());
        int dot = remainder.indexOf('.');
        return dot < 0 ? remainder : remainder.substring(0, dot);
    }
}
