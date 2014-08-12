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

package com.github.explainable.sql.table;

import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.github.explainable.sql.type.SchemaTableType;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

/**
 * Class that represents a temporary table that exists only during the execution of a single query.
 * For instance, the query {@code SELECT sid FROM (SELECT sid FROM Sailors) AS Temp} uses a
 * temporary table {@code Temp} with alias {@code Temp} and body {@code SELECT sid FROM Sailors}.
 */
public final class TempTable extends Table {
	private final SqlSelectStmt body;

	private final String alias;

	private final NestedScope scope;

	// Lazily initialized; should only be accessed using columns().
	@Nullable
	private ImmutableList<TempColumn> columns;

	TempTable(SqlSelectStmt body, String alias, NestedScope scope) {
		this.body = Preconditions.checkNotNull(body);
		this.alias = Preconditions.checkNotNull(alias);
		this.scope = Preconditions.checkNotNull(scope);
		this.columns = null;
	}

	SchemaTableType type() {
		return body.getType();
	}

	/**
	 * Initialization of the table columns occurs the first time this method is called. The return
	 * value is memoized, and the same result is returned on all subsequent calls.
	 *
	 * @return The columns in the table
	 */
	@Override
	public ImmutableList<TempColumn> columns() {
		if (columns == null) {
			ImmutableList<String> columnNames = body.columnNames();
			ImmutableList.Builder<TempColumn> columnsBuilder = ImmutableList.builder();

			for (int i = 0; i < columnNames.size(); i++) {
				columnsBuilder.add(new TempColumn(columnNames.get(i), i, this));
			}

			columns = columnsBuilder.build();
		}

		return columns;
	}

	@Override
	public String alias() {
		return alias;
	}

	NestedScope scope() {
		return scope;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("body", body)
				.toString();
	}
}
