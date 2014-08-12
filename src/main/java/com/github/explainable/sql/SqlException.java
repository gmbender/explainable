package com.github.explainable.sql;

/**
 * Exception that's thrown if an error is encountered while analyzing a parsed SQL query.
 */
public final class SqlException extends RuntimeException {
	public SqlException(String message) {
		super(message);
	}
}
