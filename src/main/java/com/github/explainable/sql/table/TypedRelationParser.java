package com.github.explainable.sql.table;

import com.github.explainable.sql.type.PrimitiveType;
import com.github.explainable.sql.type.TypeSystem;
import com.github.explainable.util.SimpleParser;

/**
 * Class that lexes and parses CREATE TABLE statements that correspond to schema definitions of
 * input tables.
 */
public final class TypedRelationParser extends SimpleParser {
	private TypedRelationParser(String input) {
		super(input);
	}

	public static TypedRelationParser create(String input) {
		return new TypedRelationParser(input);
	}

	private PrimitiveType primitiveType() {
		String typeName = literal();
		require(typeName != null);

		String normTypeName = typeName.toUpperCase();
		if (normTypeName.equals("PRIMITIVE")) {
			return TypeSystem.primitive();
		} else if (normTypeName.equals("BOOL")) {
			return TypeSystem.bool();
		} else if (normTypeName.equals("NUMERIC")) {
			return TypeSystem.numeric();
		} else if (normTypeName.equals("STRING")) {
			return TypeSystem.string();
		} else {
			throw new AssertionError("Unknown type: " + typeName);
		}
	}

	private void column(TypedRelationImpl.Builder builder) {
		spaces();
		String columnName = literal();

		require(spaces());
		PrimitiveType type = primitiveType();
		spaces();

		builder.addColumn(columnName, type);
	}

	public TypedRelation parse() {
		spaces();
		require("CREATE".equalsIgnoreCase(literal()));
		require(spaces());
		require("TABLE".equalsIgnoreCase(literal()));
		require(spaces());

		String tableName = literal();
		require(tableName != null);

		TypedRelationImpl.Builder builder = TypedRelationImpl.builder().setName(tableName);

		spaces();
		match("(");
		spaces();

		if (peek() != ')') {
			column(builder);
			while (peek() != ')') {
				match(",");
				column(builder);
			}
		}
		match(")");
		spaces();
		match(";");
		spaces();
		eoi();

		return builder.build();
	}

	public static void main(String[] args) {
		System.out.println(create("CREATE TABLE User(uid NUMERIC,\tname STRING);").parse());
	}
}
