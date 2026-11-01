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

    public ModulesController(ModuleRegistry registry) {
        throw new UnsupportedOperationException("not implemented");
    }

    @GetMapping
    public List<ModuleDescriptor> modules() {
        throw new UnsupportedOperationException("not implemented");
    }
}
