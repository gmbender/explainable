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
