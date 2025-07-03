package com.facsciences_planning_management.facsciences_planning_management.components;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

// Annotation processor that generates safe mapping methods at compile time
public class SafeMappingProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Generate null-safe mapping code at compile time
        // This would scan @SafeMapping annotated classes and generate safe methods
        return true;
    }
}
