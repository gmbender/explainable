package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.aggtype.AggTypeSystem;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.Type;
import com.github.explainable.sql.type.TypeSystem;

public final class SqlNull extends SqlExpression {
	public SqlNull() {
		// Nothing to do here.
	}

	@Override
	public void accept(SqlExpressionVisitor visitor, SqlNode parent) {
		visitor.visit(this, parent);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		return AggTypeSystem.aggOrNot();
	}

	@Override
	protected Type typeCheckImpl() {
		return TypeSystem.primitiveBottom();
	}

	@Override
	public EqualityArg equalityArg() {
		return null;
	}

	@Override
	public String toString() {
		return "NULL";
	}
}
