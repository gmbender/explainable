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

package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.aggtype.AggTypeSystem;
import com.github.explainable.sql.aggtype.NonAggregate;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.Type;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.base.Preconditions;

public final class SqlUnaryAggregate extends SqlExpression {
	private final SqlExpression argument;

	private final AggregationFunction function;

	private final boolean distinct;

	public SqlUnaryAggregate(
			SqlExpression argument,
			AggregationFunction function,
			boolean distinct) {
		this.argument = Preconditions.checkNotNull(argument);
		this.function = Preconditions.checkNotNull(function);
		this.distinct = distinct;
	}

	@Override
	public void accept(SqlExpressionVisitor visitor, SqlNode parent) {
		SqlExpressionVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			argument.accept(childVisitor, this);
		}
		visitor.leave(this, parent);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		NonAggregate nonAggregate = argument.getAggType().toNonAggregate();
		if (nonAggregate == null) {
			throw new SqlException("Must be non-aggregate: " + argument);
		}

		return AggTypeSystem.agg();
	}

	@Override
	protected Type typeCheckImpl() {
		if (function == AggregationFunction.COUNT) {
			if (argument.getType().coerceToPrimitive() == null) {
				throw new SqlException("Must be primitive: " + argument);
			}
		} else {
			if (argument.getType().coerceToNumeric() == null) {
				throw new SqlException("Must be numeric: " + argument);
			}
		}

		return TypeSystem.numeric();
	}

	@Override
	public EqualityArg equalityArg() {
		return null;
	}

	public SqlExpression argument() {
		return argument;
	}

	public AggregationFunction function() {
		return function;
	}

	@Override
	public String toString() {
		if (distinct) {
			return function + "(DISTINCT " + argument + ")";
		} else {
			return function + "(" + argument + ")";
		}
	}

	public enum AggregationFunction {
		AVG,
		COUNT,
		MAX,
		MIN,
		SUM
	}
}
