package com.github.explainable.sql.pipeline;

import com.github.explainable.sql.pipeline.passes.ViewExtractionPipeline;
import net.sf.jsqlparser.statement.select.Select;

/**
 * An analysis pipeline that applies some {@link TransformationPass}s to the abstract syntax tree of
 * a SQL query and then runs an {@link OutputPass} to obtain and return an output value of type
 * {@code T}. Pipeline construction should be handled by {@link PipelineBuilder} which does some
 * extra work to make sure that dependencies between the different pipeline stages are satisfied.
 * See {@link ViewExtractionPipeline} for an example of how this interface should be used.
 */
public interface Pipeline<T> {
	T execute(Select select);
}
