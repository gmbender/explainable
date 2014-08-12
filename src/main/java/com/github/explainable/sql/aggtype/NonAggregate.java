package com.github.explainable.sql.aggtype;

import javax.annotation.Nullable;

/**
 * An {@link AggType} for an expression such as a table column that could be referenced in the
 * {@code WHERE} clause of a SQL query.
 */
public interface NonAggregate extends AggType {
	/**
	 * By convention, any class which implements this interface must return the current object when
	 * this method is called.
	 */
	@Nullable
	@Override
	NonAggregate toNonAggregate();
}
