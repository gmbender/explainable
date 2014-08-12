package com.github.explainable.sql.type;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/13/13 Time: 2:23 PM To change this template use
 * File | Settings | File Templates.
 */
final class StringTypeImpl extends AbstractPrimitiveType implements StringType {
	@Override
	public boolean isSupertypeOf(Type type) {
		return (type instanceof StringType);
	}

	@Override
	public int hashCode() {
		return 100731401; // Randomly generated constant
	}

	@Override
	public boolean equals(Object o) {
		return (o == this) || (o instanceof StringTypeImpl);
	}

	@Override
	public String toString() {
		return "STRING";
	}

	@Nullable
	@Override
	public StringType coerceToString() {
		return this;
	}

	@Override
	public PrimitiveType commonSupertype(PrimitiveType type) {
		if (TypeSystem.string().isSupertypeOf(type)) {
			return TypeSystem.string();
		} else {
			return TypeSystem.primitive();
		}
	}
}
