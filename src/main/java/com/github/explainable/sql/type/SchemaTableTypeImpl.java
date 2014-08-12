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

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/13/13 Time: 12:31 PM To change this template
 * use File | Settings | File Templates.
 */
class SchemaTableTypeImpl extends AbstractType implements SchemaTableType {
	private final RowCount rowCount;

	private final ImmutableList<PrimitiveType> columnTypes;

	SchemaTableTypeImpl(RowCount rowCount, List<? extends PrimitiveType> columnTypes) {
		// Instantiated using a static method.
		this.rowCount = Preconditions.checkNotNull(rowCount);
		this.columnTypes = ImmutableList.copyOf(columnTypes);
	}

	@Override
	public RowCount rowCount() {
		return rowCount;
	}

	@Override
	public ImmutableList<PrimitiveType> columnTypes() {
		return columnTypes;
	}

	@Nullable
	@Override
	public BoolType coerceToBool() {
		if (rowCount == RowCount.SINGLE_ROW && columnTypes.size() == 1) {
			return columnTypes.get(0).coerceToBool();
		} else {
			return null;
		}
	}

	@Nullable
	@Override
	public NumericType coerceToNumeric() {
		if (rowCount == RowCount.SINGLE_ROW && columnTypes.size() == 1) {
			return columnTypes.get(0).coerceToNumeric();
		} else {
			return null;
		}
	}

	@Nullable
	@Override
	public StringType coerceToString() {
		if (rowCount == RowCount.SINGLE_ROW && columnTypes.size() == 1) {
			return columnTypes.get(0).coerceToString();
		} else {
			return null;
		}
	}

	@Nullable
	@Override
	public PrimitiveType coerceToPrimitive() {
		if (rowCount == RowCount.SINGLE_ROW && columnTypes.size() == 1) {
			return columnTypes.get(0).coerceToPrimitive();
		} else {
			return null;
		}
	}

	@Nullable
	@Override
	public TableType coerceToTable() {
		return this;
	}

	@Nullable
	@Override
	public SchemaTableType coerceToSchemaTable() {
		return this;
	}

	@Nullable
	@Override
	public SchemaListType coerceToSchemaList() {
		if (columnTypes.size() == 1) {
			return new SchemaListTypeImpl(rowCount, columnTypes.get(0));
		} else {
			return null;
		}
	}

	private PrimitiveType commonSupertype(PrimitiveType type1, PrimitiveType type2) {
		Type supertype = type1.commonSupertype(type2);
		if (supertype == null) {
			throw new IllegalStateException("Supertype of two primitives should exist: "
					+ type1 + ", " + type2);
		}

		return (PrimitiveType) supertype;
	}

	@Nullable
	private PrimitiveType unify(PrimitiveType type1, PrimitiveType type2) {
		Type unifier = type1.unifyWith(type2);
		if (unifier == null) {
			return null;
		}

		return (PrimitiveType) unifier;
	}

	@Override
	public TableType commonSupertype(TableType type) {
		if (!(type instanceof SchemaTableType)) {
			return TypeSystem.table();
		}

		SchemaTableType otherType = (SchemaTableType) type;
		RowCount resultRowCount = rowCount.commonSupertype(otherType.rowCount());

		if (columnTypes.size() != otherType.columnTypes().size()) {
			return TypeSystem.table();
		}

		List<PrimitiveType> resultTypes = Lists.newArrayListWithCapacity(columnTypes.size());
		for (int i = 0; i < columnTypes.size(); i++) {
			resultTypes.add(commonSupertype(columnTypes.get(i), otherType.columnTypes().get(i)));
		}

		// We leave the initialization of new type to the type system in case we're dealing with a
		// list instead of a multi-column table.
		return TypeSystem.schemaTable(resultRowCount, resultTypes);
	}

	@Override
	@Nullable
	public TableType unifyWith(TableType type) {
		if (!(type instanceof SchemaTableType)) {
			return null;
		}

		SchemaTableType otherType = (SchemaTableType) type;
		RowCount resultRowCount = rowCount.unifyWith(otherType.rowCount());

		if (columnTypes.size() != otherType.columnTypes().size()) {
			return null;
		}

		List<PrimitiveType> resultTypes = Lists.newArrayListWithCapacity(columnTypes.size());
		for (int i = 0; i < columnTypes.size(); i++) {
			resultTypes.add(unify(columnTypes.get(i), otherType.columnTypes().get(i)));
		}

		// We leave the initialization of new type to the type system in case we're dealing with a
		// list instead of a multi-column table.
		return TypeSystem.schemaTable(resultRowCount, resultTypes);
	}

	@Override
	public boolean isSupertypeOf(Type type) {
		if (!(type instanceof SchemaTableType)) {
			return false;
		}

		SchemaTableType otherType = (SchemaTableType) type;

		if (!rowCount.isSupertypeOf(otherType.rowCount())) {
			return false;
		}

		if (arity() != otherType.arity()) {
			return false;
		}

		for (int i = 0; i < arity(); i++) {
			if (!columnTypes.get(i).isSupertypeOf(otherType.columnTypes().get(i))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equals(Object o) {
		// We deliberately avoid checking whether the argument is a proper subtype of the
		// SchemaTableTypeImpl. An important consequence is that an instance of SchemaTableTypeImpl
		// can be equal to one of its subclasses.
		if (o == this) {
			return true;
		}
		if (!(o instanceof SchemaTableTypeImpl)) {
			return false;
		}
		SchemaTableTypeImpl other = (SchemaTableTypeImpl) o;
		return (rowCount == other.rowCount) && columnTypes.equals(other.columnTypes);
	}

	@Override
	public int hashCode() {
		return rowCount.hashCode() ^ columnTypes.hashCode();
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("TABLE(");
		Joiner.on(", ").appendTo(builder, columnTypes);
		builder.append(") ");
		builder.append(rowCount);

		return builder.toString();
	}

	@Override
	public int arity() {
		return columnTypes.size();
	}
}
