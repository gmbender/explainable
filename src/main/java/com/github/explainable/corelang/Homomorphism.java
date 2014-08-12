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

final class Homomorphism extends ExtendableTermMap {
	Homomorphism() {
		super();
	}

	private Homomorphism(Homomorphism original) {
		super(original);
	}

	@Override
	boolean canExtend(Term source, Term target, @Nullable Term oldSource) {
		if (!source.canFold(target)) {
			return false;
		}

		return (oldSource == null) || target.canFoldFromBoth(source, oldSource);
	}

	@Override
	void didExtend(Term from, Term to) {
		// Do nothing.
	}

	@Override
	Homomorphism copy() {
		return new Homomorphism(this);
	}

	/**
	 * Determine whether there is a homomorphism from one set of atoms to another. There is one
	 * technical difference between the homomorphisms used in this method and the formal definition
	 * provided by Cohen: we do not require every distinguished variable in {@code other} to appear in
	 * the homomorphism's image. This means that a homomorphism can be extend multiple times with
	 * different atoms.
	 */
	@Nullable
	@Override
	Homomorphism extend(Atom from, Atom to) {
		return (Homomorphism) super.extend(from, to);
	}
}
