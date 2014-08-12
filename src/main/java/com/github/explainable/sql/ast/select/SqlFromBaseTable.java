package com.github.explainable.sql.ast.select;

import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.aggtype.AggTypeSystem;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.table.BaseTable;
import com.github.explainable.sql.type.Type;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Class that represents a base (i.e., non-temporary) table that appears in the {@code FROM} clause
 * of a SQL query.
 */
public final class SqlFromBaseTable extends SqlFrom {
	private final String tableName;

	@Nullable
	private final String alias;

	@Nullable
	private BaseTable baseTable;

	public SqlFromBaseTable(String tableName, @Nullable String alias) {
		this.tableName = Preconditions.checkNotNull(tableName);
		this.alias = alias;
		this.baseTable = null;
	}

	@Nullable
	public String alias() {
		return alias;
	}

	@Override
	public AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		return AggTypeSystem.nonAgg();
	}

	@Override
	public Type typeCheckImpl() {
		return TypeSystem.table();
	}

	@Override
	public void accept(SqlSelectVisitor visitor, SqlNode parent) {
		visitor.visit(this, parent);
	}

	public String tableName() {
		return tableName;
	}

	@Nullable
	@Override
	public String toString() {
		return (alias == null) ? tableName : (tableName + " AS " + alias);
	}

	@Override
	public boolean hasOnlyInnerJoins() {
		return true;
	}

	@Override
	public Set<BaseTable> dependentTables() {
		return ImmutableSet.of(baseTable);
	}

	public void setBaseTable(BaseTable baseTable) {
		this.baseTable = Preconditions.checkNotNull(baseTable);
	}

	public BaseTable getBaseTable() {
		Preconditions.checkState(baseTable != null);
		return baseTable;
	}
}
