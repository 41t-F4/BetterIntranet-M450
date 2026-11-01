package ch.tbz.betterintranet.core.error;

/** Thrown by the gateways when a message targets a module that is switched off by configuration. */
public class ModuleDisabledException extends RuntimeException {

    private final String moduleName;

    public ModuleDisabledException(String moduleName) {
        super("Module '" + moduleName + "' is disabled");
        this.moduleName = moduleName;
    }

    public String moduleName() {
        return moduleName;
    }
}
