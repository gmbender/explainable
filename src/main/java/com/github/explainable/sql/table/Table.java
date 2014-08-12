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
