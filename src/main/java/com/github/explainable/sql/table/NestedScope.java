package com.github.explainable.sql.table;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.ast.select.SqlPlainSelect;
import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

/**
 * Class representing a hierarchy of nested {@code Scope}s. This representation allows variables
 * within an inner query to refer to columns defined by the parent, as in {@code SELECT S.sid FROM
 * Sailors S WHERE EXISTS ( SELECT * FROM Reserves R WHERE R.sid = S.sid) } In this case, the inner
 * and outer query would each correspond to a {code NestedScope}; the outer query's scope would be
 * the parent of the inner query's scope.
 */
public final class NestedScope {
	// The select statement associated with the current scope.
	private final SqlPlainSelect select;

	private final FlatScope scope;

	@Nullable
	private final NestedScope parent;

	private NestedScope(SqlPlainSelect select, @Nullable NestedScope parent) {
		this.select = Preconditions.checkNotNull(select);
		this.scope = new FlatScope();
		this.parent = parent;
	}

	public static NestedScope create(SqlPlainSelect select, @Nullable NestedScope parent) {
		return new NestedScope(select, parent);
	}

	public Column findColumn(@Nullable String tableAlias, String columnName) {
		Column column = scope.findColumn(tableAlias, columnName);

		if (column != null) {
			return column;
		}

		if (parent != null) {
			return parent.findColumn(tableAlias, columnName);
		}

		String fullName = (tableAlias == null) ? columnName : (tableAlias + '.' + columnName);
		throw new SqlException("Couldn't resolve column: " + fullName);
	}

	public ImmutableList<Column> columnsInTable(String tableAlias) {
		ImmutableList<Column> tableColumns = scope.columnsInTable(tableAlias);

		if (tableColumns != null) {
			return tableColumns;
		}

		if (parent != null) {
			return parent.columnsInTable(tableAlias);
		}

		throw new SqlException("Couldn't resolve table: " + tableAlias);
	}

	public ImmutableList<Column> localColumns() {
		return scope.allColumns();
	}

	public ImmutableList<BaseColumn> localBaseColumns() {
		return scope.baseColumns();
	}

	public ImmutableList<BaseTable> localBaseTables() {
		return scope.baseTables();
	}

	public BaseTable createBaseTable(TypedRelation relation, String alias) {
		BaseTable table = BaseTable.create(relation, alias, this);
		return scope.addBaseTable(table);
	}

	public TempTable createTemporaryTable(SqlSelectStmt sourceTable, String alias) {
		TempTable table = new TempTable(sourceTable, alias, this);
		return scope.addTemporaryTable(table);
	}

	public SqlPlainSelect select() {
		return select;
	}
}
