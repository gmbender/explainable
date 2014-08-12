package com.github.explainable.sql.ast.select;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.aggtype.AggTypeSystem;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.table.Column;
import com.github.explainable.sql.type.PrimitiveType;
import com.github.explainable.sql.type.RowCount;
import com.github.explainable.sql.type.SchemaTableType;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * Class that represents represents all the columns in a particular table in a SQL query such as
 * {@code SELECT S.* FROM Sailors S, Boats B}.
 */
public final class SqlSelectAllColumnsInTable extends SqlSelectItem {
	private final String tableAlias;

	private ImmutableList<Column> columns;

	public SqlSelectAllColumnsInTable(String tableAlias) {
		this.tableAlias = Preconditions.checkNotNull(tableAlias);
		this.columns = ImmutableList.of();
	}

	@Override
	public ImmutableList<String> columnNames() {
		List<String> outputNames = Lists.newArrayList();
		for (Column column : columns) {
			outputNames.add(column.name());
		}
		return ImmutableList.copyOf(outputNames);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		AggType resultType = AggTypeSystem.aggOrNot();
		for (Column column : columns) {
			resultType = resultType.commonSupertype(typeForColumn.getAggType(column));
			if (resultType == null) {
				throw new SqlException("Aggregate/Non-Aggregate mismatch: " + this);
			}
		}

		return resultType;
	}

	@Override
	protected SchemaTableType typeCheckImpl() {
		List<PrimitiveType> outputTypes = Lists.newArrayList();
		for (Column column : columns) {
			outputTypes.add(column.type());
		}
		return TypeSystem.schemaTable(RowCount.UNLIMITED_ROWS, outputTypes);
	}

	public String tableAlias() {
		return tableAlias;
	}

	public ImmutableList<Column> getColumns() {
		return columns;
	}

	public void setColumns(Collection<Column> columns) {
		this.columns = ImmutableList.copyOf(columns);
	}

	@Override
	public String toString() {
		return tableAlias + ".*";
	}

	@Override
	public void accept(SqlSelectVisitor visitor, SqlNode parent) {
		visitor.visit(this, parent);
	}
}
