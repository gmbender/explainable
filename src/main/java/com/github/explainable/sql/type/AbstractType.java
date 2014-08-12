package com.github.explainable.sql.type;

import javax.annotation.Nullable;

/**
 * Abstract supertype of all types used by the SQL type checker. Technically speaking, it is not
 * itself a type, although its subclasses are.
 */
abstract class AbstractType implements Type {
	AbstractType() {
	}

	@Override
	@Nullable
	public BoolType coerceToBool() {
		return null;
	}

	@Override
	@Nullable
	public NumericType coerceToNumeric() {
		return null;
	}

	@Override
	@Nullable
	public StringType coerceToString() {
		return null;
	}

	@Override
	@Nullable
	public PrimitiveType coerceToPrimitive() {
		return null;
	}

	@Override
	@Nullable
	public TableType coerceToTable() {
		return null;
	}

	@Override
	@Nullable
	public SchemaTableType coerceToSchemaTable() {
		return null;
	}

	@Override
	@Nullable
	public SchemaListType coerceToSchemaList() {
		return null;
	}
}
