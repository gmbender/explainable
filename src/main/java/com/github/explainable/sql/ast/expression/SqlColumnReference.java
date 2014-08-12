/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Gabriel Bender
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
