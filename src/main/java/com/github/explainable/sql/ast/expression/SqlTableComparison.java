package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.SchemaListType;
import com.github.explainable.sql.type.Type;
import com.google.common.base.Preconditions;

public final class SqlTableComparison extends SqlExpression {
	// TODO: Ensure that ANY and ALL are only used in the context of an (in)equality comparison.
	private final SqlExpression right;

	private final SqlTableComparisonKind kind;

	public SqlTableComparison(SqlExpression right, SqlTableComparisonKind kind) {
		this.right = Preconditions.checkNotNull(right);
		this.kind = Preconditions.checkNotNull(kind);
	}

	@Override
	public void accept(SqlExpressionVisitor visitor, SqlNode parent) {
		SqlExpressionVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			right.accept(childVisitor, this);
		}
		visitor.leave(this, parent);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		return right.getAggType();
	}

	@Override
	protected Type typeCheckImpl() {
		SchemaListType rightType = right.getType().coerceToSchemaList();
		if (rightType == null) {
			throw new SqlException("Must be list: " + rightType);
		}

		return rightType.columnType();
	}

	@Override
	public EqualityArg equalityArg() {
		return null;
	}

	public SqlExpression right() {
		return right;
	}

	public SqlTableComparisonKind kind() {
		return kind;
	}

	@Override
	public String toString() {
		return kind + " " + right;
	}

	public enum SqlTableComparisonKind {
		ANY,
		ALL
	}
}
