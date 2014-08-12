/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Gabriel Bender
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
