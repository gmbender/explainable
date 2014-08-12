package com.github.explainable.sql.aggtype;

import javax.annotation.Nullable;

/**
 * Class representing the type of an expression that can only be used as an aggregate, such as
 * {@code COUNT(DISTINCT uid)}.
 */
final class AggregateImpl implements Aggregate {
	@Nullable
	@Override
	public Aggregate toAggregate() {
		return this;
	}

	@Nullable
	@Override
	public NonAggregate toNonAggregate() {
		return null;
	}

	@Nullable
	@Override
	public AggType commonSupertype(AggType other) {
		return (other instanceof Aggregate) ? this : null;
	}

	@Override
	public int hashCode() {
		return 1197619746; // Randomly generated constant
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof AggregateImpl);
	}

	@Override
	public String toString() {
		return "AGGREGATE";
	}
}
