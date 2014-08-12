package com.github.explainable.sql.type;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/23/13 Time: 9:35 AM To change this template use
 * File | Settings | File Templates.
 */
public interface Type {
	@Nullable
	BoolType coerceToBool();

	@Nullable
	NumericType coerceToNumeric();

	@Nullable
	StringType coerceToString();

	@Nullable
	PrimitiveType coerceToPrimitive();

	@Nullable
	TableType coerceToTable();

	@Nullable
	SchemaTableType coerceToSchemaTable();

	@Nullable
	SchemaListType coerceToSchemaList();

	/**
	 * Determine whether the current object is a supertype of the specified type.
	 *
	 * @param type The type that we want to compare against
	 * @return {@code true} if the current object is a supertype, and {@code false} otherwise
	 */
	boolean isSupertypeOf(Type type);
}
