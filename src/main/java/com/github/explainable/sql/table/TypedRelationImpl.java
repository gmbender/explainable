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
