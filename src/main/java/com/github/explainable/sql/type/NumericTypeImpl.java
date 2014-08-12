package com.github.explainable.sql.type;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/13/13 Time: 12:30 PM To change this template
 * use File | Settings | File Templates.
 */
final class NumericTypeImpl extends AbstractPrimitiveType implements NumericType {
	@Override
	public boolean isSupertypeOf(Type type) {
		return (type instanceof NumericType);
	}

	@Override
	public int hashCode() {
		return 1315476958; // Randomly generated constant
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof NumericTypeImpl);
	}

	@Override
	public String toString() {
		return "NUMERIC";
	}

	@Nullable
	@Override
	public NumericType coerceToNumeric() {
		return this;
	}

	@Override
	public PrimitiveType commonSupertype(PrimitiveType type) {
		if (isSupertypeOf(type)) {
			return TypeSystem.numeric();
		} else {
			return TypeSystem.primitive();
		}
	}
}
