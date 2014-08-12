package com.github.explainable.sql.ast.select;

import com.github.explainable.sql.ast.SqlNode;

/**
 * Abstract superclass of non-expression nodes that make up the {@code SELECT} clause of a query.
 */
public abstract class SqlSelect extends SqlNode {
	public abstract void accept(SqlSelectVisitor visitor, SqlNode parent);
}
