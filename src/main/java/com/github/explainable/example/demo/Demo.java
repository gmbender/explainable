package com.github.explainable.example.demo;

import net.sf.jsqlparser.JSQLParserException;

/**
 * Abstract interface for an interactive command-line demo in which the end user types in a line of
 * SQL code and expects the demo to analyze it and spit out some insightful results.
 */
public interface Demo {
	/**
	 * Give the user a help message with some information to help them get started, like the database
	 * schema.
	 */
	void showHelpMessage(DemoRunner runner);

	/**
	 * Analyze the specified SQL query.
	 *
	 * @throws JSQLParserException if an error occurs while analyzing the query
	 */
	void handleQuery(String sql, DemoRunner runner) throws JSQLParserException;

	/**
	 * Reset the current object's internal state to its initial value. This method may be used as an
	 * error recovery mechanism, so it should assume that the object's current state is arbitrarily
	 * corrupted.
	 */
	void reset();
}
