package ch.tbz.betterintranet.core.web;

import ch.tbz.betterintranet.core.module.ModuleDescriptor;
import ch.tbz.betterintranet.core.module.ModuleRegistry;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Tells the frontend which modules are enabled so it can hide navigation entries. */
@RestController
@RequestMapping("/api/modules")
public class ModulesController {

    private final ModuleRegistry registry;

    public ModulesController(ModuleRegistry registry) {
        this.registry = registry;
    }

    @GetMapping
    public List<ModuleDescriptor> modules() {
        return registry.describeAll();
    }
}
