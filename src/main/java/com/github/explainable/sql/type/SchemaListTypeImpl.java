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
