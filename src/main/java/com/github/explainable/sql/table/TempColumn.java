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

import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.type.PrimitiveType;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

/**
 * Class that represents a single column of a {@link TempTable}.
 */
public class TempColumn extends Column {
	private final String name;

	private final int columnIndex;

	private final TempTable parent;

	TempColumn(String name, int columnIndex, TempTable parent) {
		this.name = Preconditions.checkNotNull(name);
		this.columnIndex = columnIndex;
		this.parent = Preconditions.checkNotNull(parent);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public TempTable parent() {
		return parent;
	}

	@Override
	public PrimitiveType type() {
		return parent.type().columnTypes().get(columnIndex);
	}

	@Override
	public EqualityArg equalityArg() {
		return null;
	}

	@Nullable
	@Override
	public NestedScope scope() {
		return parent.scope();
	}

	@Override
	public String toString() {
		return name + " @ " + columnIndex;
	}
}
