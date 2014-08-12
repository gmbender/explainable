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

package com.github.explainable.benchmark.preparedstmt;

import com.github.explainable.corelang.Term;
import com.github.explainable.corelang.Terms;
import com.github.explainable.util.SimpleParser;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Class that lexes and parses PostgreSQL-style EXECUTE commands for prepared statements.
 *
 * @see <a href="http://www.postgresql.org/docs/current/static/sql-prepare.html">
 * http://www.postgresql.org/docs/current/static/sql-prepare.html</a>
 */
final class ExecStmtParser extends SimpleParser {
	// Warning: graceful error handling is not yet implemented, and lexing and parsing errors may
	// result in undefined behavior.

	private ExecStmtParser(String query) {
		super(query);
	}

	public static ExecStmtParser create(String query) {
		return new ExecStmtParser(query);
	}

	private Term argument() {
		Long longResult = integer();
		if (longResult != null) {
			return Terms.constant(longResult);
		}

		String stringResult = string();
		if (stringResult != null) {
			return Terms.constant(stringResult);
		}

		return null;
	}

	private List<Term> argumentList() {
		List<Term> result = Lists.newArrayList();

		Term arg = argument();
		while (arg != null) {
			result.add(arg);
			spaces();
			if (just(',')) {
				spaces();
				arg = argument();
				Preconditions.checkState(arg != null);
			} else {
				arg = null;
			}
		}

		return result;
	}

	public SqlExec parse() {
		spaces();
		match("EXECUTE");
		spaces();
		String procedureName = literal();
		spaces();
		Preconditions.checkArgument(just('('));
		spaces();
		List<Term> arguments = argumentList();
		Preconditions.checkArgument(just(')'));
		spaces();
		Preconditions.checkArgument(just(';'));
		spaces();
		eoi();

		return SqlExec.create(procedureName, arguments);
	}
}
