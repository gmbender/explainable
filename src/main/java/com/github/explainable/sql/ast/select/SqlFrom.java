package com.github.explainable.sql.ast.select;

import com.github.explainable.sql.constraint.EqualityConstraint;
import com.github.explainable.sql.table.BaseTable;
import com.github.explainable.util.LDVertex;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Abstract superclass that represents a table that appears in the {@code FROM} clause of a SQL
 * query or a join of two or more such tables.
 */
public abstract class SqlFrom extends SqlSelect {
	@Nullable
	private LDVertex<Object, EqualityConstraint> conditionVertex;

	SqlFrom() {
		this.conditionVertex = null;
	}

	public abstract boolean hasOnlyInnerJoins();

	public final LDVertex<Object, EqualityConstraint> getConditionVertex() {
		if (conditionVertex == null) {
			throw new IllegalStateException();
		}
		return conditionVertex;
	}

	public final void setConditionVertex(LDVertex<Object, EqualityConstraint> vertex) {
		conditionVertex = Preconditions.checkNotNull(vertex);
	}

	public abstract Set<BaseTable> dependentTables();
}
