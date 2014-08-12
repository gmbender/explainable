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
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Created by gbender on 12/5/13.
 */
final class SqlExec {
	private final String procedureName;

	private final ImmutableList<Term> arguments;

	private SqlExec(String procedureName, List<? extends Term> arguments) {
		this.procedureName = procedureName;
		this.arguments = ImmutableList.copyOf(arguments);
	}

	public static SqlExec create(String statementName, List<? extends Term> arguments) {
		return new SqlExec(statementName, arguments);
	}

	String statementName() {
		return procedureName;
	}

	ImmutableList<Term> arguments() {
		return arguments;
	}

	@Override
	public int hashCode() {
		return procedureName.hashCode() + 17 * arguments.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SqlExec)) {
			return false;
		}
		SqlExec other = (SqlExec) obj;
		return procedureName.equals(other.procedureName)
				&& arguments.equals(other.arguments);
	}

	@Override
	public String toString() {
		return "EXECUTE " + statementName() + "(" + Joiner.on(", ").join(arguments) + ");";
	}
}
