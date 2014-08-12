package com.github.explainable.sql.ast.select;

import com.github.explainable.sql.type.SchemaTableType;
import com.google.common.collect.ImmutableList;

/**
 * Class that represents a single expression that makes up part of a SQL query's output. For
 * instance, in the query {@code S.*, B.bid FROM Sailors S, Boats B}, {@code *} and {@code B.bid}
 * are represented by different {@code SqlSelectItem}s.
 */
public abstract class SqlSelectItem extends SqlSelect {
	public abstract ImmutableList<String> columnNames();

	@Override
	public SchemaTableType getType() {
		return (SchemaTableType) super.getType();
	}
}
