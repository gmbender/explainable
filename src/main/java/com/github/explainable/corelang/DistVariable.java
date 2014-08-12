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

import javax.annotation.Nullable;

public final class DistVariable extends Term {
	private static final int UNIFICATION_PRIORITY = 1;

	DistVariable() {
	}

	@Override
	int unificationPriority() {
		return UNIFICATION_PRIORITY;
	}

	@Nullable
	@Override
	public Term unifyWithSameType(Term term) {
		if (!(term instanceof DistVariable)) {
			throw new IllegalArgumentException("unifyWithSameType: Not a constant: " + term);
		}

		// Both variables are structurally equivalent; we can choose one arbitrarily.
		return this;
	}

	@Override
	boolean canSpecialize(Term target, TermType oldMultisetImageType) {
		return true;
	}

	@Override
	boolean canSpecializeFromBoth(Term source, Term oldSource) {
		return true;
	}

	@Override
	public boolean canFold(Term target) {
		return (target.type() == TermType.DIST_VARIABLE);
	}

	@Override
	boolean canFoldFromBoth(Term source, Term oldSource) {
		TermType sourceType = source.type();
		TermType oldSourceType = oldSource.type();
		return (sourceType != TermType.DIST_VARIABLE) || (oldSourceType != TermType.DIST_VARIABLE);
	}

	@Override
	public TermType type() {
		return TermType.DIST_VARIABLE;
	}

	@Override
	public String toString() {
		return "Dist[" + hashCode() + "]";
	}
}
