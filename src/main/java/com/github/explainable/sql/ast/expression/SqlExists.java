package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.Type;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.base.Preconditions;

public final class SqlExists extends SqlExpression {
	private final SqlSubSelect subSelect;

	private final boolean not;

	public SqlExists(SqlSubSelect subSelect, boolean not) {
		this.subSelect = Preconditions.checkNotNull(subSelect);
		this.not = not;
	}

	@Override
	public void accept(SqlExpressionVisitor visitor, SqlNode parent) {
		SqlExpressionVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			subSelect.accept(childVisitor, this);
		}
		visitor.leave(this, parent);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		return subSelect.getAggType();
	}

	@Override
	protected Type typeCheckImpl() {
		if (subSelect.getType().coerceToTable() == null) {
			throw new SqlException("Must be table: " + subSelect);
		}
		return TypeSystem.bool();
	}

	@Override
	public EqualityArg equalityArg() {
		return null;
	}

	public SqlSubSelect subSelect() {
		return subSelect;
	}

	public boolean isNot() {
		return not;
	}

	@Override
	public String toString() {
		String notString = not ? "NOT " : "";
		return "(" + notString + "EXISTS " + subSelect.toString() + ")";
	}
}
