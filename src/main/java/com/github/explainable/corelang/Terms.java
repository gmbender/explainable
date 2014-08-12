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

/**
 * Static factory for creating new {@link Term}s.
 */
public final class Terms {
	private static int counter = 0;

	private Terms() {
		throw new UnsupportedOperationException("Can't instantiated Terms");
	}

	/**
	 * Create a fresh distinguished variable.
	 */
	public static DistVariable dist() {
		return new DistVariable();
	}

	/**
	 * Create a fresh multiset-existential variable.
	 */
	public static MultisetVariable multiset() {
		return new MultisetVariable();
	}

	/**
	 * Create a fresh set-existential variable.
	 */
	public static SetVariable set() {
		return new SetVariable();
	}

	/**
	 * Create a constant with the specified value.
	 */
	public static Constant constant(Object value) {
		return new Constant(value);
	}

	/**
	 * Create a new term with the specified type.
	 */
	public static Term variableWithType(TermType type) {
		Preconditions.checkNotNull(type);
		Term result;

		switch (type) {
			case DIST_VARIABLE:
				result = dist();
				break;
			case MULTISET_VARIABLE:
				result = multiset();
				break;
			case SET_VARIABLE:
				result = set();
				break;
			case CONSTANT: // Fall-through
			case NONE: // Fall-through
			default:
				throw new IllegalArgumentException("Unknown term type: " + type);
		}

		return result;
	}
}
