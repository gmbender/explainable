/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Gabriel Bender
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
