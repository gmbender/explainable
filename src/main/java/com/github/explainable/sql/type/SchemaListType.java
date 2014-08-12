package com.github.explainable.sql.type;

/**
 * Subtype of {@link SchemaTableType} that represents the type of a table with a known schema that
 * has exactly one column.
 */
public interface SchemaListType extends SchemaTableType {
	Type columnType();
}
