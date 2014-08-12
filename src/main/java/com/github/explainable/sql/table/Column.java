package com.github.explainable.sql.table;

import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.type.PrimitiveType;

import javax.annotation.Nullable;

/**
 * Interface that represents a single column of a {@link Table}.
 */
public abstract class Column {
	Column() {
		// Can only be extended by other classes in the same package.
	}

	public abstract String name();

	public abstract Table parent();

	public abstract PrimitiveType type();

	@Nullable
	public abstract EqualityArg equalityArg();

	@Nullable
	public abstract NestedScope scope();
}
