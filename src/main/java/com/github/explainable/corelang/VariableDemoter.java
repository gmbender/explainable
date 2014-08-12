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

package com.github.explainable.corelang;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

/**
 * Map that converts an ordinary query atom into a condition atom. Every constant and every term
 * that appears in the query body is mapped to itself. All other terms are demoted to fresh
 * distinguished variables.
 */
final class VariableDemoter implements TermMap {
	private final Map<Term, Term> forwardMap;

	VariableDemoter(Set<? extends Term> bodyTerms) {
		forwardMap = Maps.newHashMap();
		for (Term term : bodyTerms) {
			forwardMap.put(term, term);
		}
	}

	@Override
	public Term apply(Term from) {
		if (from.type() == TermType.CONSTANT) {
			return from;
		} else {
			Term to = forwardMap.get(from);

			if (to == null) {
				to = Terms.set();
				forwardMap.put(from, to);
			}

			return to;
		}
	}
}
