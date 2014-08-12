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
