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
