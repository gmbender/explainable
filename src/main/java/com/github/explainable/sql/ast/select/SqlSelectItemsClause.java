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
import com.github.explainable.sql.type.PrimitiveType;
import com.github.explainable.sql.type.RowCount;
import com.github.explainable.sql.type.SchemaTableType;
import com.github.explainable.sql.type.Type;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Class that represents the clause containing the output of a SQL query, such as the expression
 * {@code S.sid, B.bid} in the query {@code SELECT S.sid, B.bid FROM Sailors S, Boats B}.
 */
public class SqlSelectItemsClause extends SqlSelect {
	private final ImmutableList<SqlSelectItem> selectItems;

	public SqlSelectItemsClause(List<? extends SqlSelectItem> selectItems) {
		this.selectItems = ImmutableList.copyOf(selectItems);
	}

	@Override
	public void accept(SqlSelectVisitor visitor, SqlNode parent) {
		SqlSelectVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			for (SqlSelectItem item : selectItems) {
				item.accept(childVisitor, this);
			}
		}
		visitor.leave(this, parent);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		AggType resultType = AggTypeSystem.aggOrNot();

		for (SqlSelectItem selectItem : selectItems) {
			AggType selectItemType = selectItem.getAggType();
			resultType = resultType.commonSupertype(selectItemType);

			if (resultType == null) {
				throw new SqlException("Aggregate/Non-Aggregate mismatch: " + selectItems);
			}
		}

		return resultType;
	}

	@Override
	protected Type typeCheckImpl() {
		List<PrimitiveType> columnTypes = Lists.newArrayList();
		for (SqlSelectItem selectItem : selectItems) {
			columnTypes.addAll(selectItem.getType().columnTypes());
		}

		return TypeSystem.schemaTable(RowCount.UNLIMITED_ROWS, columnTypes);
	}

	@Override
	public SchemaTableType getType() {
		return (SchemaTableType) super.getType();
	}

	public ImmutableList<String> columnNames() {
		List<String> columnNames = Lists.newArrayList();

		for (SqlSelectItem selectItem : selectItems) {
			columnNames.addAll(selectItem.columnNames());
		}

		return ImmutableList.copyOf(columnNames);
	}

	public ImmutableList<SqlSelectItem> itemList() {
		return selectItems;
	}

	@Override
	public String toString() {
		return Joiner.on(", ").join(selectItems);
	}
}
