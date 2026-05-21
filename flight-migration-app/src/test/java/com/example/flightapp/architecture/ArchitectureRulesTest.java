package com.example.flightapp.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureRulesTest {

    private final JavaClasses classes = new ClassFileImporter().importPackages("com.example.flightapp");

    @Test
    void controllersDoNotAccessRepositoriesDirectly() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..api..")
            .should().dependOnClassesThat().resideInAPackage("..persistence..");
        rule.check(classes);
    }

    @Test
    void domainDoesNotDependOnFrameworkOrPersistence() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework.web..",
                "org.springframework.security..",
                "org.springframework.batch..",
                "..persistence.."
            );
        rule.check(classes);
    }

    @Test
    void persistenceDoesNotDependOnApi() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..persistence..")
            .should().dependOnClassesThat().resideInAPackage("..api..");
        rule.check(classes);
    }

    @Test
    void commonDoesNotDependOnFeatureModules() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..common..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..auth..",
                "..flight..",
                "..booking..",
                "..batch.."
            );
        rule.check(classes);
    }

    @Test
    void batchDoesNotDependOnControllers() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..batch..").and().resideOutsideOfPackage("..api..")
            .should().dependOnClassesThat().haveSimpleNameEndingWith("Controller");
        rule.check(classes);
    }

    @Test
    void namingMatchesPackagePlacement() {
        classes().that().haveSimpleNameEndingWith("Controller")
            .should().resideInAPackage("..api..")
            .check(classes);
        classes().that().haveSimpleNameEndingWith("Repository")
            .should().resideInAPackage("..persistence..")
            .check(classes);
        classes().that().haveSimpleNameEndingWith("ApplicationService")
            .should().resideInAPackage("..application..")
            .check(classes);
    }
}
