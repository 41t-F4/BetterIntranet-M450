package ch.tbz.betterintranet.core.module;

/** Public view of a module: its name (package segment under the base package) and whether it is enabled. */
public record ModuleDescriptor(String name, boolean enabled) {
}
