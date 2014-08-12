package com.github.explainable.corelang;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Class that represents a database relation in our core query language.
 */
public final class RelationImpl implements Relation {
	private final String tableName;

	private final ImmutableList<String> fields;

	private RelationImpl(String tableName, List<String> fields) {
		this.tableName = Preconditions.checkNotNull(tableName);
		this.fields = ImmutableList.copyOf(fields);
	}


	public static RelationImpl create(String tableName, List<String> fields) {
		return new RelationImpl(tableName, fields);
	}

	@Override
	public String name() {
		return tableName;
	}

	@Override
	public int arity() {
		return fields.size();
	}

	@Override
	public ImmutableList<String> columnNames() {
		return fields;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RelationImpl)) {
			return false;
		}

		RelationImpl other = (RelationImpl) o;
		return tableName.equals(other.tableName) && fields.equals(other.fields);
	}

	@Override
	public int hashCode() {
		return tableName.hashCode() + 37 * fields.hashCode();
	}

	@Override
	public String toString() {
		return tableName + "(" + Joiner.on(", ").join(fields) + ")";
	}
}
