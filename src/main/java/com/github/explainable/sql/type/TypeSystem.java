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

import java.util.List;

/**
 * This class serves as a static factory for classes representing types in the default SQL type
 * system.
 */
public final class TypeSystem {
	private static final PrimitiveType PRIMITIVE = new PrimitiveTypeImpl();

	private static final BoolType BOOL = new BoolTypeImpl();

	private static final PrimitiveBottomType PRIMITIVE_BOTTOM = new PrimitiveBottomTypeImpl();

	private static final NumericType NUMERIC = new NumericTypeImpl();

	private static final StringType STRING = new StringTypeImpl();

	private static final TableType TABLE = new TableTypeImpl();

	private TypeSystem() {
		// Prevent the class from being accidentally instantiated using reflection.
		throw new UnsupportedOperationException("Cannot instantiate TypeSystem");
	}

	public static PrimitiveType primitive() {
		return PRIMITIVE;
	}

	public static PrimitiveBottomType primitiveBottom() {
		return PRIMITIVE_BOTTOM;
	}

	public static BoolType bool() {
		return BOOL;
	}

	public static NumericType numeric() {
		return NUMERIC;
	}

	public static StringType string() {
		return STRING;
	}

	public static TableType table() {
		return TABLE;
	}

	public static SchemaTableType schemaTable(
			RowCount rowCount,
			List<? extends PrimitiveType> columnTypes) {
		if (columnTypes.size() == 1) {
			return schemaList(rowCount, columnTypes.get(0));
		} else {
			return new SchemaTableTypeImpl(rowCount, columnTypes);
		}
	}

	public static SchemaListType schemaList(RowCount rowCount, PrimitiveType columnType) {
		return new SchemaListTypeImpl(rowCount, columnType);
	}
}
