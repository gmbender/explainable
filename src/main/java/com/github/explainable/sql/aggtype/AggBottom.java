package com.github.explainable.sql.aggtype;

/**
 * Intersection type of {@link Aggregate} and {@link NonAggregate}. This is an {@link AggType} for
 * an expression such as a constant that could be used in either the {@code HAVING} or the {@code
 * WHERE} clause of a SQL query.
 */
public interface AggBottom extends Aggregate, NonAggregate {
}
