package com.github.explainable.sql.ast.select;

import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.expression.SqlExpression;
import com.github.explainable.sql.ast.expression.SqlExpressionVisitor;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.Type;
import com.google.common.base.Preconditions;

/**
 * Class that represents the {@code ON} clause of a relational join in the {@code FROM} clause of a
 * sql query.
 */
public final class SqlOnClause extends SqlSelect {
	private final SqlExpression body;

	public SqlOnClause(SqlExpression body) {
		this.body = Preconditions.checkNotNull(body);
	}

	@Override
	public void accept(SqlSelectVisitor visitor, SqlNode parent) {
		SqlExpressionVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			body.accept(childVisitor, this);
		}
		visitor.leave(this, parent);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		return body.getAggType();
	}

	@Override
	protected Type typeCheckImpl() {
		return body.getType();
	}

	@Override
	public String toString() {
		return " ON " + body;
	}
}
