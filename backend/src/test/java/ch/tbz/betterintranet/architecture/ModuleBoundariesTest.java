package ch.tbz.betterintranet.architecture;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Enforces the modular monolith rules from CLAUDE.md section 4.3. Every feature module lives in its own
 * package below {@code ch.tbz.betterintranet}; modules talk to each other only via the gateways and the
 * public {@code api} packages.
 */
@AnalyzeClasses(packages = "ch.tbz.betterintranet", importOptions = ImportOption.DoNotIncludeTests.class)
class ModuleBoundariesTest {

    @ArchTest
    static final ArchRule modules_only_depend_on_core_and_other_modules_api_packages = slices()
            .matching("ch.tbz.betterintranet.(*)..")
            .should().notDependOnEachOther()
            .ignoreDependency(alwaysTrue(), resideInAPackage("ch.tbz.betterintranet.core.."))
            .ignoreDependency(alwaysTrue(), resideInAPackage("..api.."))
            .as("a module may only use core and other modules' 'api' packages");

    @ArchTest
    static final ArchRule core_does_not_depend_on_feature_modules = noClasses()
            .that().resideInAPackage("ch.tbz.betterintranet.core..")
            .should().dependOnClassesThat(
                    resideInAPackage("ch.tbz.betterintranet..")
                            .and(resideOutsideOfPackage("ch.tbz.betterintranet.core..")))
            .as("core must not know any feature module")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule controllers_only_talk_to_gateways = noClasses()
            .that().resideInAPackage("..web..")
            .and().resideOutsideOfPackage("ch.tbz.betterintranet.core..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..application..", "..infrastructure..")
            .as("controllers are thin and go through the gateways")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule infrastructure_is_hidden_from_web = noClasses()
            .that().resideInAPackage("..web..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule handlers_live_in_application_packages = classes()
            .that().implement("ch.tbz.betterintranet.core.gateway.QueryHandler")
            .or().implement("ch.tbz.betterintranet.core.gateway.CommandHandler")
            .should().resideInAPackage("..application..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule no_cycles_between_modules = slices()
            .matching("ch.tbz.betterintranet.(*)..")
            .should().beFreeOfCycles();
}
