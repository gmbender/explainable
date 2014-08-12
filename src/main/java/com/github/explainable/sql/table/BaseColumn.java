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

package com.github.explainable.sql.table;

import com.github.explainable.corelang.Term;
import com.github.explainable.sql.constraint.BaseColumnArg;
import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.type.PrimitiveType;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

/**
 * Class that represents a single column of a {@link BaseTable}.
 */
public final class BaseColumn extends Column {
	private final String name;

	private final BaseTable parent;

	private final PrimitiveType type;

	// Lazily initialized the first time it's needed in order to break a cyclic dependency between
	// the constructor of this object and the constructor of {@link BaseColumnArg}.
	@Nullable
	private EqualityArg equalityArg;

	@Nullable
	private Term term;

	/**
	 * Create a new table column with the specified name and parent. In general, this constructor
	 * shouldn't be called directly.
	 */
	BaseColumn(String name, BaseTable parent, PrimitiveType type) {
		this.name = Preconditions.checkNotNull(name);
		this.parent = Preconditions.checkNotNull(parent);
		this.type = Preconditions.checkNotNull(type);
		this.equalityArg = null;
		this.term = null;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public BaseTable parent() {
		return parent;
	}

	@Override
	public PrimitiveType type() {
		return type;
	}

	@Override
	public EqualityArg equalityArg() {
		if (equalityArg == null) {
			equalityArg = BaseColumnArg.of(this);
		}
		return equalityArg;
	}

	public Term getTerm() {
		Preconditions.checkState(term != null);
		return term;
	}

	public void setTerm(Term term) {
		this.term = Preconditions.checkNotNull(term);
	}

	@Nullable
	@Override
	public NestedScope scope() {
		return parent.scope();
	}

	@Override
	public String toString() {
		return name + ": " + type;
	}
}
