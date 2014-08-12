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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

/**
 * Abstract class that represents a constant in a conjunctive query.
 */
public final class Constant extends Term {
	private static final int UNIFICATION_PRIORITY = 0;

	private final Object value;

	Constant(Object value) {
		this.value = Preconditions.checkNotNull(value);
	}

	@Override
	int unificationPriority() {
		return UNIFICATION_PRIORITY;
	}

	@Nullable
	@Override
	public Term unifyWithSameType(Term term) {
		if (!(term instanceof Constant)) {
			throw new IllegalArgumentException("unifyWithSameType: Not a constant: " + term);
		}

		return value.equals(((Constant) term).value) ? this : null;
	}

	@Override
	boolean canSpecialize(Term target, TermType oldMultisetImageType) {
		return equals(target);
	}

	@Override
	boolean canSpecializeFromBoth(Term source, Term oldSource) {
		return true;
	}

	@Override
	public boolean canFold(Term target) {
		return equals(target);
	}

	@Override
	boolean canFoldFromBoth(Term source, Term oldSource) {
		return true;
	}

	@Override
	public TermType type() {
		return TermType.CONSTANT;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * Two constants are equal if and only if their corresponding values are equal.
	 */
	@Override
	public boolean equals(Object other) {
		return (other instanceof Constant) && value.equals(((Constant) other).value);
	}

	@Override
	public String toString() {
		return (value instanceof String) ? ("\'" + value + "\'") : value.toString();
	}
}
