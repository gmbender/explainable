package com.github.explainable.sql.ast.select;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.aggtype.AggTypeSystem;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.table.BaseTable;
import com.github.explainable.sql.type.Type;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Class representing sub-select in the {@code FROM} clause of a SQL query, such as the {@code
 * SELECT ... AS Temp} clause of {@code SELECT sid FROM (SELECT * FROM Sailors) AS Temp}.
 */
public final class SqlFromSubSelect extends SqlFrom {
	private final SqlSelectStmt body;

	private final String alias;

	public SqlFromSubSelect(SqlSelectStmt body, String alias) {
		this.body = Preconditions.checkNotNull(body);
		this.alias = Preconditions.checkNotNull(alias);
	}

	public SqlSelectStmt body() {
		return body;
	}

	public String alias() {
		return alias;
	}

	@Override
	public AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		return AggTypeSystem.agg();
	}

	@Override
	public Type typeCheckImpl() {
		if (body.getType().coerceToTable() == null) {
			throw new SqlException("Not a table: " + body);
		}
		return TypeSystem.table();
	}

	@Override
	public String toString() {
		return body + " AS " + alias;
	}

	@Override
	public void accept(SqlSelectVisitor visitor, SqlNode parent) {
		SqlSelectVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			body.accept(childVisitor, this);
		}
		visitor.leave(this, parent);
	}

	@Override
	public boolean hasOnlyInnerJoins() {
		return true;
	}

	@Override
	public Set<BaseTable> dependentTables() {
		return ImmutableSet.of();
	}
}
