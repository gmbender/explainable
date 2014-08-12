package com.github.explainable.sql.ast;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.Type;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

/**
 * Abstract superclass of all nodes in the abstract syntax tree of a parsed SQL query.
 */
public abstract class SqlNode {
	@Nullable
	private AggType aggType;

	@Nullable
	private Type type;

	protected SqlNode() {
		this.aggType = null;
		this.type = null;
	}

	protected abstract AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn);

	/**
	 * Check the expression rooted at the current node to make sure that it doesn't try to mix
	 * aggregates and non-aggregates in an illegal way. Here's an example of the type of query we're
	 * trying to prevent: {@code SELECT sid, MAX(age) FROM Sailors}. If the expression successfully
	 * aggregate type-checks then the node's aggregate type will be cached for future use. This method
	 * should only be called if all the node's children have already been type-checked.
	 *
	 * @throws com.github.explainable.sql.SqlException if the node doesn't aggregate type-check
	 * @throws IllegalStateException if fields required by the agg type-checker haven't been set
	 */
	public final void aggTypeCheck(AggTypeForColumn typeForColumn) {
		assert aggType == null;
		aggType = Preconditions.checkNotNull(aggTypeCheckImpl(typeForColumn));
	}

	/**
	 * Get the aggregate type returned by the most recent call to {@link #aggTypeCheck}.
	 *
	 * @throws SqlException if {@link #aggTypeCheck} has not yet been called.
	 */
	public AggType getAggType() {
		if (aggType == null) {
			throw new SqlException("AggType hasn't yet been set for expression: " + this);
		}

		return aggType;
	}

	/**
	 * Type-check the expression rooted at the current node to make sure that it doesn't do anything
	 * crazy, like try to use a string as a number. If the expression successfully type-checks then the
	 * node's type will be cached for future use. This method should only be called if all the node's
	 * children have already been type-checked.
	 *
	 * @throws com.github.explainable.sql.SqlException if the node doesn't type-check
	 * @throws IllegalStateException if fields required by the type-checker haven't been set
	 */
	protected abstract Type typeCheckImpl();

	public final void typeCheck() {
		assert type == null;
		type = Preconditions.checkNotNull(typeCheckImpl());
	}

	/**
	 * Get the type returned by the most recent call to {@link #typeCheck}.
	 *
	 * @throws SqlException if {@link #typeCheck} has not yet been called.
	 */
	public Type getType() {
		if (type == null) {
			throw new SqlException("Type hasn't yet been set for expression: " + this);
		}

		return type;
	}
}
