package com.github.explainable.sql.aggtype;

import javax.annotation.Nullable;

/**
 * Default implementation of {@link AggBottom}.
 */
final class AggBottomImpl implements AggBottom {
	@Nullable
	@Override
	public Aggregate toAggregate() {
		return this;
	}

	@Nullable
	@Override
	public NonAggregate toNonAggregate() {
		return this;
	}

	@Nullable
	@Override
	public AggType commonSupertype(AggType other) {
		return other;
	}

	@Override
	public int hashCode() {
		return 1840855486; // Randomly generated constant
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof AggBottomImpl);
	}

	@Override
	public String toString() {
		return "AGGREGATE_OR_NOT";
	}
}
