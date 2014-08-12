package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.Type;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.base.Preconditions;

public final class SqlIn extends SqlExpression {
	private final SqlExpression left;

	private final SqlExpression right;

	private final boolean not;

	public SqlIn(SqlExpression left, SqlExpression right, boolean not) {
		this.left = Preconditions.checkNotNull(left);
		this.right = Preconditions.checkNotNull(right);
		this.not = not;
	}

	@Override
	public void accept(SqlExpressionVisitor visitor, SqlNode parent) {
		SqlExpressionVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			left.accept(childVisitor, this);
			right.accept(childVisitor, this);
		}
		visitor.leave(this, parent);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		AggType resultType = left.getAggType().commonSupertype(right.getAggType());
		if (resultType == null) {
			throw new SqlException("Aggregate/non-aggregate mismatch: " + left + " and " + right);
		}

		return resultType;
	}

	@Override
	protected Type typeCheckImpl() {
		if (left.getType().coerceToPrimitive() == null) {
			throw new SqlException("Must be primitive: " + left);
		}

		if (right.getType().coerceToSchemaList() == null) {
			throw new SqlException("Must be list: " + right);
		}

		return TypeSystem.bool();
	}

	@Override
	public EqualityArg equalityArg() {
		return null;
	}

	public SqlExpression left() {
		return left;
	}

	public SqlExpression right() {
		return right;
	}

	public boolean isNot() {
		return not;
	}

	@Override
	public String toString() {
		String notString = not ? " NOT" : "";
		return "(" + left.toString() + notString + " IN " + right.toString() + ")";
	}
}
