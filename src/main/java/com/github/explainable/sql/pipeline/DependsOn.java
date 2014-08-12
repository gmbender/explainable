package com.github.explainable.sql.pipeline;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that's used to model dependencies between different pipeline stages. If {@link
 * TransformationPass} subclass X must be run before subclass Y in the pipeline then Y should be
 * annotated with {@code @DependsOn(X.class)}. The the value associated with the annotation can
 * either be a single dependency or a list of dependencies.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface DependsOn {
	Class<? extends TransformationPass>[] value();
}
