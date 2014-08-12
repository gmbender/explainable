package com.github.explainable.sql.pipeline;

import com.github.explainable.sql.ast.select.SqlSelectStmt;

/**
 * Interface representing the final stage of an {@link Pipeline}, which traverses the AST of a SQL
 * query and makes some modifications to its state.
 */
public interface TransformationPass {
	/**
	 * Traverse {@code select} and make some modifications to its state. This method should not affect
	 * the externally visible state of the current {@code TransformationPass} object.
	 */
	void execute(SqlSelectStmt select);
}
