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

final class Specialization extends ExtendableTermMap {
	private TermType multisetImageType;

	Specialization() {
		super();
		this.multisetImageType = TermType.NONE;
	}

	private Specialization(Specialization other) {
		super(other);
		this.multisetImageType = other.multisetImageType;
	}

	@Override
	boolean canExtend(Term source, Term target, @Nullable Term oldSource) {
		if (!source.canSpecialize(target, multisetImageType)) {
			return false;
		}

		return (oldSource == null) || target.canSpecializeFromBoth(source, oldSource);
	}

	@Override
	void didExtend(Term from, Term to) {
		if (from.type() == TermType.MULTISET_VARIABLE) {
			multisetImageType = to.type();
		}
	}

	@Override
	Specialization copy() {
		return new Specialization(this);
	}

	@Nullable
	@Override
	Specialization extend(Atom from, Atom to) {
		return (Specialization) super.extend(from, to);
	}
}
