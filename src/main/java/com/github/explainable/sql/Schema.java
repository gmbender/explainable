package com.github.explainable.sql;

import com.github.explainable.sql.table.TypedRelation;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public final class Schema {
	private final ImmutableList<TypedRelation> relations;

	private Schema(List<? extends TypedRelation> relations) {
		this.relations = ImmutableList.copyOf(relations);
	}

	public static Schema of(TypedRelation... relations) {
		return new Schema(Arrays.asList(relations));
	}

	public static Schema create(List<? extends TypedRelation> relations) {
		return new Schema(relations);
	}

	@Nullable
	public TypedRelation findRelation(String tableName) {
		Preconditions.checkNotNull(tableName);

		for (TypedRelation relation : relations) {
			if (relation.name().equals(tableName)) {
				return relation;
			}
		}

		return null;
	}

	public ImmutableList<TypedRelation> relations() {
		return relations;
	}

	@Override
	public String toString() {
		return Joiner.on(System.getProperty("line.separator")).join(relations);
	}
}
