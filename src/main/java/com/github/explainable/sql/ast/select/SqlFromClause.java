package com.github.explainable.sql.ast.select;

import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.Type;
import com.google.common.base.Preconditions;

/**
 * Class that represents the FROM clause of a SQL query.
 */
public class SqlFromClause extends SqlSelect {
	private final SqlFrom from;

	public SqlFromClause(SqlFrom from) {
		this.from = Preconditions.checkNotNull(from);
	}

	@Override
	public void accept(SqlSelectVisitor visitor, SqlNode parent) {
		SqlSelectVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			if (from != null) {
				from.accept(childVisitor, this);
			}
		}
		visitor.leave(this, parent);
	}

	public SqlFrom from() {
		return from;
	}

	@Override
	public Type typeCheckImpl() {
		return from.getType();
	}

	@Override
	public AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		return from.getAggType();
	}

	public boolean hasOnlyInnerJoins() {
		return from.hasOnlyInnerJoins();
	}

	@Override
	public String toString() {
		return " FROM " + from;
	}
}
