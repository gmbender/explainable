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

package com.github.explainable.sql.type;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/13/13 Time: 2:15 PM To change this template use
 * File | Settings | File Templates.
 */
final class TableTypeImpl extends AbstractType implements TableType {
	TableTypeImpl() {
	}

	@Nullable
	@Override
	public TableType coerceToTable() {
		return this;
	}

	@Override
	public boolean isSupertypeOf(Type type) {
		return (type instanceof TableType);
	}

	@Override
	public int hashCode() {
		return 5;
	}

	@Override
	public boolean equals(Object o) {
		return (o == this) || (o instanceof TableTypeImpl);
	}

	@Override
	public String toString() {
		return "TABLE";
	}

	@Override
	public TableType commonSupertype(TableType type) {
		return TypeSystem.table();
	}

	@Nullable
	@Override
	public TableType unifyWith(TableType type) {
		return type;
	}
}
