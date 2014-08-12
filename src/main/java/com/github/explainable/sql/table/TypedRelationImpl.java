package com.github.explainable.sql.table;

import com.github.explainable.corelang.Relation;
import com.github.explainable.corelang.RelationImpl;
import com.github.explainable.sql.type.PrimitiveType;
import com.github.explainable.sql.type.RowCount;
import com.github.explainable.sql.type.SchemaTableType;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Default implementation of {@link TypedRelation}.
 */
public final class TypedRelationImpl implements TypedRelation {
	private final Relation relation;

	private final SchemaTableType type;

	private TypedRelationImpl(Relation relation, SchemaTableType type) {
		this.relation = Preconditions.checkNotNull(relation);
		this.type = Preconditions.checkNotNull(type);
		Preconditions.checkArgument(relation.arity() == type.arity());
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String name() {
		return relation.name();
	}

	@Override
	public int arity() {
		return relation.arity();
	}

	@Override
	public ImmutableList<String> columnNames() {
		return relation.columnNames();
	}

	@Override
	public SchemaTableType type() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TypedRelationImpl)) {
			return false;
		}

		TypedRelationImpl other = (TypedRelationImpl) o;
		return relation.equals(other.relation) && type.equals(other.type);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(relation.name());
		result.append("(");

		for (int i = 0; i < relation.arity(); i++) {
			result.append(relation.columnNames().get(i));
			result.append(": ");
			result.append(type.columnTypes().get(i));

			if (i < relation.arity() - 1) {
				result.append(", ");
			}
		}

		result.append(")");
		return result.toString();
	}

	public static final class Builder {
		private String name;

		private List<String> columnNames;

		private List<PrimitiveType> columnTypes;

		private Builder() {
			this.name = null;
			this.columnNames = Lists.newArrayList();
			this.columnTypes = Lists.newArrayList();
		}

		public String getName() {
			return name;
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder addColumn(String name, PrimitiveType type) {
			columnNames.add(name);
			columnTypes.add(type);
			return this;
		}

		public TypedRelationImpl build() {
			Preconditions.checkState(name != null, "Relation name has not been set");
			Relation relation = RelationImpl.create(name, columnNames);
			SchemaTableType type = TypeSystem.schemaTable(RowCount.UNLIMITED_ROWS, columnTypes);
			return new TypedRelationImpl(relation, type);
		}
	}
}
