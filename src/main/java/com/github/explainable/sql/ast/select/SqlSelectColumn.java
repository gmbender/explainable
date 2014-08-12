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
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.expression.SqlExpression;
import com.github.explainable.sql.ast.expression.SqlExpressionVisitor;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.PrimitiveType;
import com.github.explainable.sql.type.RowCount;
import com.github.explainable.sql.type.SchemaTableType;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

/**
 * Class that represents a single column in the output of a SQL query, such as the column {@code
 * S.sid} in the query {@code SELECT S.sid FROM Sailors S, Boats B}.
 */
public final class SqlSelectColumn extends SqlSelectItem {
	private final SqlExpression value;

	@Nullable
	private final String alias;

	public SqlSelectColumn(SqlExpression value, @Nullable String alias) {
		this.value = Preconditions.checkNotNull(value);
		this.alias = alias;
	}

	@Override
	public ImmutableList<String> columnNames() {
		return (alias != null) ? ImmutableList.of(alias) : ImmutableList.<String>of();
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		return value.getAggType();
	}

	@Override
	public SchemaTableType typeCheckImpl() {
		PrimitiveType valueType = value.getType().coerceToPrimitive();
		if (valueType == null) {
			throw new SqlException("Must be primitive: " + value);
		}

		return TypeSystem.schemaTable(RowCount.UNLIMITED_ROWS, ImmutableList.of(valueType));
	}

	@Override
	public String toString() {
		return (alias == null) ? value.toString() : (value + " AS " + alias);
	}

	public SqlExpression value() {
		return value;
	}

	@Nullable
	public String alias() {
		return alias;
	}

	@Override
	public void accept(SqlSelectVisitor visitor, SqlNode parent) {
		SqlExpressionVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			value.accept(childVisitor, this);
		}
		visitor.leave(this, parent);
	}
}
