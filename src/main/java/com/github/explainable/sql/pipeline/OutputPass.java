package com.github.explainable.sql.pipeline;

import com.github.explainable.sql.ast.select.SqlSelectStmt;

/**
 * Interface representing the final stage of an {@link Pipeline}, which takes the pipeline's current
 * state and uses it to generate some externally visible output.
 */
public interface OutputPass<T> {
	/**
	 * Traverse {@code select} and generate an output that will be externally visible. This method
	 * should not affect the externally visible state of the {@code TransformationPass} object,
	 * although it is permitted to affect the state of {@code select}.
	 */
	T execute(SqlSelectStmt select);
}
