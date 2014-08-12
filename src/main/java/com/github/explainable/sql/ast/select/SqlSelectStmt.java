package com.github.explainable.sql.ast.select;

import com.github.explainable.sql.type.SchemaTableType;
import com.google.common.collect.ImmutableList;

/**
 * Root class representing an arbitrary SQL query (which may include set operations such as {@code
 * UNION} or {@code INTERSECT}).
 */
public abstract class SqlSelectStmt extends SqlSelect {
	public abstract ImmutableList<String> columnNames();

	@Override
	public SchemaTableType getType() {
		return (SchemaTableType) super.getType();
	}
}
