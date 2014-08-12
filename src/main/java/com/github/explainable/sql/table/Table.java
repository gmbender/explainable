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

import com.github.explainable.sql.SqlException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

/**
 * Interface that represents a table instance. The table can either come from the underlying
 * database schema or can be a temporary table that exists only during the execution of a single
 * query. It's possible to have multiple instances of the same table, each with its own object. For
 * instance, in the query {@code SELECT sid FROM Table T1, Table T2}, the {@code Table} has two
 * different instances with aliases {@code T1} and {@code T2}.
 */
public abstract class Table {
	Table() {
		// Can only be extended by classes in the same package.
	}

	public abstract ImmutableList<? extends Column> columns();

	/**
	 * Find the column with the specified name.
	 *
	 * @param columnName the name of the column we wish to find
	 * @return the column with the specified name, or {@link null} if no suitable column was found
	 * @throws SqlException if the table contains two or more columns with the specified name
	 */
	@Nullable
	public final Column findColumn(String columnName) {
		Preconditions.checkNotNull(this);
		Preconditions.checkNotNull(columnName);

		Column candidate = null;

		for (Column column : columns()) {
			if (column.name().equals(columnName)) {
				if (candidate != null) {
					throw new SqlException("Ambiguous column name: " + columnName);
				}

				candidate = column;
			}
		}

		return candidate;
	}

	public abstract String alias();
}
