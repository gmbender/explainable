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

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

/**
 * Default implementation of {@link SchemaListType}.
 */
final class SchemaListTypeImpl extends SchemaTableTypeImpl implements SchemaListType {
	SchemaListTypeImpl(RowCount rowCount, PrimitiveType columnType) {
		super(rowCount, ImmutableList.of(columnType));
	}

	@Override
	public Type columnType() {
		return columnTypes().get(0);
	}

	@Nullable
	@Override
	public SchemaListType coerceToSchemaList() {
		return this;
	}

	// We deliberately avoid overriding the default implementations of equals(...) and hashCode()
	// from SchemaTableTypeImpl.  This means that a SchemaTableType containing exactly one column
	// will be equal to a SchemaListType with the same column type and row type.
}
