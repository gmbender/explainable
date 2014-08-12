package com.github.explainable.sql.table;

import com.github.explainable.sql.SqlException;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that keeps track of which tables and columns instances are defined in a SELECT statement.
 */
final class FlatScope {
	// Map that takes a table name and returns the corresponding table declaration.
	private final Map<String, Table> tablesByName;

	// Map that takes an unqualified column name and returns the corresponding table column(s).
	private final SetMultimap<String, Column> columnsByName;

	// List of all the columns in the input tables (including both base tables and temporary tables)
	// that are referenced in the current scope.
	private final List<Column> allColumns;

	// List of all the columns in the current scope that belong to base tables.
	private final List<BaseColumn> allBaseColumns;

	// List of all the base tables in the current scope.
	private final List<BaseTable> allBaseTables;

	// List of tables whose alias and column names haven't been added to the indices above yet.
	private final List<Table> unregisteredTemporaryTables;

	FlatScope() {
		this.tablesByName = Maps.newHashMap();
		this.columnsByName = HashMultimap.create();
		this.allColumns = Lists.newArrayList();
		this.allBaseColumns = Lists.newArrayList();
		this.allBaseTables = Lists.newArrayList();
		this.unregisteredTemporaryTables = Lists.newArrayList();
	}

	private void registerTable(Table table) {
		Preconditions.checkNotNull(table);
		String tableAlias = table.alias();

		if (tablesByName.containsKey(tableAlias)) {
			throw new SqlException("Table alias is already in use: " + tableAlias);
		}

		tablesByName.put(tableAlias, table);

		for (Column column : table.columns()) {
			allColumns.add(column);
			columnsByName.put(column.name(), column);
		}
	}

	private void registerNewTables() {
		for (Table newTable : unregisteredTemporaryTables) {
			registerTable(newTable);
		}

		unregisteredTemporaryTables.clear();
	}

	BaseTable addBaseTable(BaseTable table) {
		registerTable(table);
		allBaseTables.add(table);

		for (BaseColumn column : table.columns()) {
			allBaseColumns.add(column);
		}

		return table;
	}

	TempTable addTemporaryTable(TempTable table) {
		unregisteredTemporaryTables.add(table);
		return table;
	}

	/**
	 * Returns all columns in the current scope.
	 */
	ImmutableList<Column> allColumns() {
		registerNewTables();
		return ImmutableList.copyOf(allColumns);
	}

	ImmutableList<BaseColumn> baseColumns() {
		registerNewTables();
		return ImmutableList.copyOf(allBaseColumns);
	}

	ImmutableList<BaseTable> baseTables() {
		registerNewTables();
		return ImmutableList.copyOf(allBaseTables);
	}

	@Nullable
	ImmutableList<Column> columnsInTable(String tableAlias) {
		Preconditions.checkNotNull(tableAlias);
		registerNewTables();

		if (tablesByName.containsKey(tableAlias)) {
			return ImmutableList.copyOf(tablesByName.get(tableAlias).columns());
		} else {
			return null;
		}
	}

	@Nullable
	Column findColumn(@Nullable String tableAlias, String columnName) {
		Preconditions.checkNotNull(columnName);
		registerNewTables();

		if (tableAlias != null) {
			Table table = tablesByName.get(tableAlias);
			return (table == null) ? null : table.findColumn(columnName);
		} else {
			Set<Column> resultSet = columnsByName.get(columnName);

			if (resultSet.size() > 1) {
				throw new SqlException("Ambiguous column name: " + columnName);
			}

			return (resultSet.size() == 1) ? resultSet.iterator().next() : null;
		}
	}

	@Override
	public String toString() {
		return tablesByName.toString();
	}
}
