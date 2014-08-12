package com.github.explainable.sql.table;

import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.type.PrimitiveType;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

/**
 * Class that represents a single column of a {@link TempTable}.
 */
public class TempColumn extends Column {
	private final String name;

	private final int columnIndex;

	private final TempTable parent;

	TempColumn(String name, int columnIndex, TempTable parent) {
		this.name = Preconditions.checkNotNull(name);
		this.columnIndex = columnIndex;
		this.parent = Preconditions.checkNotNull(parent);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public TempTable parent() {
		return parent;
	}

	@Override
	public PrimitiveType type() {
		return parent.type().columnTypes().get(columnIndex);
	}

	@Override
	public EqualityArg equalityArg() {
		return null;
	}

	@Nullable
	@Override
	public NestedScope scope() {
		return parent.scope();
	}

	@Override
	public String toString() {
		return name + " @ " + columnIndex;
	}
}
