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
import com.github.explainable.corelang.TermMap;
import com.github.explainable.corelang.Terms;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Given a list of arguments (T1, T2, ..., Tn), map the constant '$1' to T1, the constant '$2' to
 * T2, and so on.
 */
final class ExecSubstitutionMap implements TermMap {
	private final ImmutableMap<Term, Term> substitutions;

	private ExecSubstitutionMap(Map<Term, Term> substitutions) {
		this.substitutions = ImmutableMap.copyOf(substitutions);
	}

	public static ExecSubstitutionMap create(List<Term> arguments) {
		Map<Term, Term> substitutions = Maps.newHashMap();
		for (int i = 0; i < arguments.size(); i++) {
			substitutions.put(Terms.constant("$" + (i + 1)), arguments.get(i));
		}
		return new ExecSubstitutionMap(substitutions);
	}

	@Override
	public Term apply(Term from) {
		Term to = substitutions.get(from);
		return (to == null) ? from : to;
	}

	@Override
	public String toString() {
		return substitutions.toString();
	}
}
