package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.table.Column;
import com.github.explainable.sql.type.Type;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

public final class SqlColumnReference extends SqlExpression {
	private final String columnName;

	@Nullable
	private final String tableAlias;

	@Nullable
	private Column column;

	public SqlColumnReference(String columnName, @Nullable String tableAlias) {
		this.columnName = Preconditions.checkNotNull(columnName);
		this.tableAlias = tableAlias;
		this.column = null;
	}

	@Override
	public void accept(SqlExpressionVisitor visitor, SqlNode parent) {
		visitor.visit(this, parent);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		return typeForColumn.getAggType(getColumn());
	}

	@Override
	protected Type typeCheckImpl() {
		return getColumn().type();
	}

	@Override
	public EqualityArg equalityArg() {
		return getColumn().equalityArg();
	}

	public String columnName() {
		return columnName;
	}

	@Nullable
	public String tableAlias() {
		return tableAlias;
	}

	/**
	 * Get the table column that the current object refers to.
	 *
	 * @return the column
	 * @throws IllegalArgumentException if the column has not been set.
	 */
	public Column getColumn() {
		if (column == null) {
			throw new IllegalStateException("Column has not been set: " + column);
		}
		return column;
	}

	public void setColumn(Column column) {
		this.column = Preconditions.checkNotNull(column);
	}

	@Override
	public String toString() {
		return (tableAlias == null) ? columnName : (tableAlias + "." + columnName);
	}
}
