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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Class that represents represents all the columns in the current scope (i.e., {@code SELECT *}) in
 * a SQL query such as {@code SELECT * FROM Sailors S, Boats B}.
 */
public final class SqlSelectAllColumns extends SqlSelectItem {
	@Nullable
	private ImmutableList<Column> columns;

	public SqlSelectAllColumns() {
		this.columns = ImmutableList.of();
	}

	@Override
	public ImmutableList<String> columnNames() {
		List<String> outputNames = Lists.newArrayList();
		for (Column column : getColumns()) {
			outputNames.add(column.name());
		}
		return ImmutableList.copyOf(outputNames);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		AggType resultType = AggTypeSystem.aggOrNot();
		for (Column column : getColumns()) {
			resultType = resultType.commonSupertype(typeForColumn.getAggType(column));
			if (resultType == null) {
				throw new SqlException("Aggregate/Non-Aggregate mismatch: SELECT " + this);
			}
		}

		return resultType;
	}

	@Override
	protected SchemaTableType typeCheckImpl() {
		List<PrimitiveType> outputTypes = Lists.newArrayList();
		for (Column column : getColumns()) {
			outputTypes.add(column.type());
		}
		return TypeSystem.schemaTable(RowCount.UNLIMITED_ROWS, outputTypes);
	}

	public ImmutableList<Column> getColumns() {
		if (columns == null) {
			throw new IllegalStateException("Columns haven't been initialized in: " + this);
		}
		return columns;
	}

	public void setColumns(List<Column> tableColumns) {
		this.columns = ImmutableList.copyOf(tableColumns);
	}

	@Override
	public String toString() {
		return "*";
	}

	@Override
	public void accept(SqlSelectVisitor visitor, SqlNode parent) {
		visitor.visit(this, parent);
	}
}
