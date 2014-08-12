package com.github.explainable.sql.pipeline;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * A utility class that is used for constructing a new {@link Pipeline} and validating the
 * dependency constraints between the stages.
 */
public final class PipelineBuilder<T> {
	private final List<TransformationPass> transformations;

	private final Set<Class<? extends TransformationPass>> transformationClasses;

	@Nullable
	private OutputPass<T> output;

	private PipelineBuilder() {
		this.transformations = Lists.newArrayList();
		this.transformationClasses = Sets.newHashSet();
		this.output = null;
	}

	public static <T> PipelineBuilder<T> create() {
		return new PipelineBuilder<T>();
	}

	private void verifyDependencies(Class<?> stageClass) {
		for (Annotation annotation : stageClass.getAnnotations()) {
			if (annotation instanceof DependsOn) {
				Class<? extends TransformationPass>[] dependencies
						= ((DependsOn) annotation).value();

				for (Class<? extends TransformationPass> dependency : dependencies) {
					Preconditions.checkArgument(
							transformationClasses.contains(dependency),
							"Unsatisfied dependency: " + stageClass.getSimpleName()
									+ " depends on " + dependency.getSimpleName());
				}

				break;
			}
		}
	}

	public PipelineBuilder<T> addTransformation(TransformationPass stage) {
		if (output != null) {
			throw new UnsupportedOperationException(
					"Cannot add transformation stages after the output stage");
		}

		verifyDependencies(stage.getClass());

		transformations.add(stage);
		transformationClasses.add(stage.getClass());
		return this;
	}

	public PipelineBuilder<T> setOutput(OutputPass<T> stage) {
		Preconditions.checkNotNull(stage);

		if (output != null) {
			throw new UnsupportedOperationException(
					"Cannot change the output stage once it's been set");
		}

		verifyDependencies(stage.getClass());

		output = stage;
		return this;
	}

	@Nullable
	public Pipeline<T> build() {
		if (output == null) {
			throw new UnsupportedOperationException("Output stage has not been set");
		}

		return new PipelineImpl<T>(transformations, output);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("transformations", transformations)
				.add("output", output)
				.toString();
	}
}
