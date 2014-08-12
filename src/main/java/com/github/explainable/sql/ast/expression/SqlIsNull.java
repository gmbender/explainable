package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.Type;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.base.Preconditions;

public final class SqlIsNull extends SqlExpression {
	private final SqlExpression body;

	private final boolean not;

	public SqlIsNull(SqlExpression body, boolean not) {
		this.body = Preconditions.checkNotNull(body);
		this.not = not;
	}

	@Override
	public void accept(SqlExpressionVisitor visitor, SqlNode parent) {
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
		if (body.getType().coerceToPrimitive() == null) {
			throw new SqlException("Must be primitive: " + body);
		}
		return TypeSystem.bool();
	}

	@Override
	public EqualityArg equalityArg() {
		return null;
	}

	@Override
	public String toString() {
		return "(" + body.toString() + " IS" + (not ? " NOT" : "") + " NULL)";
	}
}
