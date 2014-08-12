package com.github.explainable.sql.type;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/13/13 Time: 2:15 PM To change this template use
 * File | Settings | File Templates.
 */
final class TableTypeImpl extends AbstractType implements TableType {
	TableTypeImpl() {
	}

	@Nullable
	@Override
	public TableType coerceToTable() {
		return this;
	}

	@Override
	public boolean isSupertypeOf(Type type) {
		return (type instanceof TableType);
	}

	@Override
	public int hashCode() {
		return 5;
	}

	@Override
	public boolean equals(Object o) {
		return (o == this) || (o instanceof TableTypeImpl);
	}

	@Override
	public String toString() {
		return "TABLE";
	}

	@Override
	public TableType commonSupertype(TableType type) {
		return TypeSystem.table();
	}

	@Nullable
	@Override
	public TableType unifyWith(TableType type) {
		return type;
	}
}
