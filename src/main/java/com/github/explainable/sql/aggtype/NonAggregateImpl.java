package com.github.explainable.sql.aggtype;

import javax.annotation.Nullable;

/**
 * Default implementation of {@link NonAggregate}.
 */
final class NonAggregateImpl implements NonAggregate {
	@Nullable
	@Override
	public Aggregate toAggregate() {
		return null;
	}

	@Nullable
	@Override
	public NonAggregate toNonAggregate() {
		return this;
	}

	@Nullable
	@Override
	public AggType commonSupertype(AggType other) {
		return (other instanceof NonAggregate) ? this : null;
	}

	@Override
	public int hashCode() {
		return -1041826677; // Randomly generated constant
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof NonAggregateImpl);
	}

	@Override
	public String toString() {
		return "NON_AGGREGATE";
	}
}
