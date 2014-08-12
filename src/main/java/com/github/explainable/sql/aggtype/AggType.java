package com.github.explainable.sql.aggtype;

import javax.annotation.Nullable;

/**
 * Abstract supertype of all aggregation types. Technically, {@code AggType} is not itself a valid
 * type, but its direct descendants {@link Aggregate} and {@link NonAggregate} are.
 */
public interface AggType {
	@Nullable
	Aggregate toAggregate();

	@Nullable
	NonAggregate toNonAggregate();

	@Nullable
	AggType commonSupertype(AggType other);
}
