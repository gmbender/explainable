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
