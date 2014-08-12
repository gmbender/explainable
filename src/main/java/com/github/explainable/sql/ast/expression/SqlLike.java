package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.Type;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.base.Preconditions;

/**
 * AST node corresponding to the SQL LIKE expression.
 */
public final class SqlLike extends SqlExpression {
	private final SqlExpression left;

	private final SqlExpression right;

	private final boolean isNot;

	public SqlLike(SqlExpression left, SqlExpression right, boolean isNot) {
		this.left = Preconditions.checkNotNull(left);
		this.right = Preconditions.checkNotNull(right);
		this.isNot = isNot;
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
	public EqualityArg equalityArg() {
		return null;
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
		if (left.getType().coerceToString() == null) {
			throw new SqlException("Must be numeric: " + left);
		}
		if (right.getType().coerceToString() == null) {
			throw new SqlException("Must be numeric: " + right);
		}
		return TypeSystem.bool();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(left);
		if (isNot) {
			result.append(" NOT");
		}
		result.append(" LIKE ");
		result.append(right);

		return result.toString();
	}
}
