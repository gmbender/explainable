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
