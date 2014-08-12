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
