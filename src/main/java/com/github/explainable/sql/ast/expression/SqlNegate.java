package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.Type;
import com.google.common.base.Preconditions;

public final class SqlNegate extends SqlExpression {
	private final SqlExpression value;

	public SqlNegate(SqlExpression value) {
		this.value = Preconditions.checkNotNull(value);
	}

	@Override
	public void accept(SqlExpressionVisitor visitor, SqlNode parent) {
		SqlExpressionVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			value.accept(childVisitor, this);
		}
		visitor.leave(this, parent);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		return value.getAggType();
	}

	@Override
	protected Type typeCheckImpl() {
		Type valueType = value.getType();
		if (valueType.coerceToNumeric() == null) {
			throw new SqlException("Must be numeric: " + value);
		}
		return valueType;
	}

	@Override
	public EqualityArg equalityArg() {
		return null;
	}

	@Override
	public String toString() {
		return "-" + value.toString();
	}

	public SqlExpression value() {
		return value;
	}
}
