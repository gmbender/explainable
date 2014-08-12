package com.github.explainable.sql.aggtype;

import javax.annotation.Nullable;

/**
 * An {@link AggType} for an expression such as {@code COUNT}(*) that could be referenced in a the
 * {@code HAVING} clause of a SQL query.
 */
public interface Aggregate extends AggType {
	/**
	 * By convention, any class which implements this interface must return the current object when
	 * this method is called.
	 */
	@Nullable
	@Override
	Aggregate toAggregate();
}
