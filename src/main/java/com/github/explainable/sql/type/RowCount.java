package com.github.explainable.sql.type;

/**
 * Constraint on the number of rows in a table.
 */
public enum RowCount {
	/**
	 * Table is guaranteed to contain exactly one row.
	 */
	SINGLE_ROW,

	/**
	 * Table contains zero or more rows.
	 */
	UNLIMITED_ROWS;


	public RowCount commonSupertype(RowCount other) {
		return (this == UNLIMITED_ROWS || other == UNLIMITED_ROWS) ? UNLIMITED_ROWS : SINGLE_ROW;
	}


	public RowCount unifyWith(RowCount other) {
		return (this == SINGLE_ROW || other == SINGLE_ROW) ? SINGLE_ROW : UNLIMITED_ROWS;
	}

	public boolean isSupertypeOf(RowCount other) {
		return (this == UNLIMITED_ROWS) || (other == SINGLE_ROW);
	}
}
