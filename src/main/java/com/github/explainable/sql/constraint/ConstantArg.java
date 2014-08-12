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

package com.github.explainable.sql.constraint;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 10/28/13 Time: 1:42 PM To change this template
 * use File | Settings | File Templates.
 */
public final class ConstantArg extends EqualityArg {
	private final Object value;

	private ConstantArg(Object value) {
		this.value = Preconditions.checkNotNull(value);
	}

	public static ConstantArg of(Object value) {
		return new ConstantArg(value);
	}

	public Object value() {
		return value;
	}

	@Override
	void matchLeft(EqualityConstraintMatcher matcher, EqualityArg right) {
		right.matchRight(matcher, this);
	}

	@Override
	void matchRight(EqualityConstraintMatcher matcher, ConstantArg left) {
		matcher.match(left, this);
	}

	@Override
	void matchRight(EqualityConstraintMatcher matcher, BaseColumnArg left) {
		matcher.match(left, this);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (!(other instanceof ConstantArg)) {
			return false;
		}

		ConstantArg otherConstantArg = (ConstantArg) other;
		return value.equals(otherConstantArg.value);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("value", value)
				.toString();
	}
}
