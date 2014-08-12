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

import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.github.explainable.sql.converter.SelectStatementConverter;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import net.sf.jsqlparser.statement.select.Select;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Default implementation of {@link Pipeline}. This class shouldn't be used directly; instead,
 * pipelines should be constructed using {@link PipelineBuilder}.
 */
final class PipelineImpl<T> implements Pipeline<T> {
	private final SelectStatementConverter converter;

	private final ImmutableList<TransformationPass> transformations;

	private final OutputPass<T> output;

	PipelineImpl(List<? extends TransformationPass> transformations, OutputPass<T> output) {
		this.converter = SelectStatementConverter.create();
		this.transformations = ImmutableList.copyOf(transformations);
		this.output = output;
	}

	@Nullable
	@Override
	public T execute(Select select) {
		SqlSelectStmt converted = converter.convert(select);

		for (TransformationPass stage : transformations) {
			stage.execute(converted);
		}

		return output.execute(converted);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("transformations", transformations)
				.add("output", output)
				.toString();
	}
}
