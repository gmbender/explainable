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
