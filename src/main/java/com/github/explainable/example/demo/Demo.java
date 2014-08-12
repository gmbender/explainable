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
