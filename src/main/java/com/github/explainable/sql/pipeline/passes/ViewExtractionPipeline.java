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

package com.github.explainable.sql.pipeline.passes;

import com.github.explainable.corelang.View;
import com.github.explainable.sql.Schema;
import com.github.explainable.sql.pipeline.Pipeline;
import com.github.explainable.sql.pipeline.PipelineBuilder;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.sf.jsqlparser.statement.select.Select;

/**
 * Class that takes the AST for a SQL {@code SELECT} statement and generates a list of atoms with
 * the property that the answers to all the atoms in the set together uniquely determine the answer
 * to the original query on any possible dataset.
 */
public final class ViewExtractionPipeline implements Pipeline<ImmutableList<View>> {
	private final Pipeline<ImmutableList<View>> pipeline;

	private ViewExtractionPipeline(Pipeline<ImmutableList<View>> pipeline) {
		this.pipeline = Preconditions.checkNotNull(pipeline);
	}

	@Override
	public ImmutableList<View> execute(Select select) {
		return pipeline.execute(select);
	}

	public static ViewExtractionPipeline create(Schema schema) {
		Pipeline<ImmutableList<View>> pipeline = PipelineBuilder.<ImmutableList<View>>create()
				.addTransformation(new ColumnResolver(schema))
				.addTransformation(new CorrelatedColumnFinder())
				.addTransformation(new AggTypeChecker())
				.addTransformation(new TypeChecker())
				.addTransformation(new TermInitializer())
				.addTransformation(new CondGraphInitializer())
				.addTransformation(new CondGraphAnnotator())
				.setOutput(new ViewExtractor())
				.build();

		return new ViewExtractionPipeline(pipeline);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("pipeline", pipeline)
				.toString();
	}
}
