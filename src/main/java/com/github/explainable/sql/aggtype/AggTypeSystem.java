package com.github.explainable.sql.aggtype;

/**
 * Static factory for {@link AggType} and its subclasses. This type system allows us to distinguish
 * between {@link Aggregate} types that could be used in the {@code HAVING} clause of a SQL query
 * and {@link NonAggregate} types tht could be used in the {@code WHERE} clause of a SQL query.
 */
public final class AggTypeSystem {
	private static final Aggregate AGGREGATE = new AggregateImpl();

	private static final NonAggregate NON_AGG = new NonAggregateImpl();

	private static final AggBottom AGG_OR_NOT = new AggBottomImpl();

	private AggTypeSystem() {
		// Prevent the class from being accidentally instantiated using reflection.
		throw new UnsupportedOperationException("AggTypeSystem cannot be instantiated");
	}

	public static Aggregate agg() {
		return AGGREGATE;
	}

	public static NonAggregate nonAgg() {
		return NON_AGG;
	}

	public static AggBottom aggOrNot() {
		return AGG_OR_NOT;
	}
}
