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

import com.github.explainable.sql.table.BaseColumn;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 10/28/13 Time: 1:51 PM To change this template
 * use File | Settings | File Templates.
 */
public final class BaseColumnArg extends EqualityArg {
	private final BaseColumn column;

	private BaseColumnArg(BaseColumn column) {
		this.column = Preconditions.checkNotNull(column);
	}

	public static BaseColumnArg of(BaseColumn column) {
		return new BaseColumnArg(column);
	}

	public BaseColumn column() {
		return column;
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
		return column.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (!(other instanceof BaseColumnArg)) {
			return false;
		}

		BaseColumnArg otherColumnArg = (BaseColumnArg) other;
		return column.equals(otherColumnArg.column);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("column", column)
				.toString();
	}
}
