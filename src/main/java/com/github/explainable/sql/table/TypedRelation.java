package com.github.explainable.sql.table;

import com.github.explainable.corelang.Relation;
import com.github.explainable.sql.type.SchemaTableType;

/**
 * Extension of the {@link Relation} interface that assigns a {@link SchemaTableType} to the
 * relation. This is essential for type-checking, since it allows us to figure out the types of all
 * the relation columns in the database schema.
 */
public interface TypedRelation extends Relation {
	SchemaTableType type();
}
