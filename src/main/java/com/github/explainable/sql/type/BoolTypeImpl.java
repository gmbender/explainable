package com.github.explainable.sql.type;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/13/13 Time: 12:30 PM To change this template
 * use File | Settings | File Templates.
 */
final class BoolTypeImpl extends AbstractPrimitiveType implements BoolType {
	@Override
	public boolean isSupertypeOf(Type type) {
		return (type instanceof BoolType);
	}

	@Override
	public int hashCode() {
		return -609888843; // Randomly generated constant
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof BoolTypeImpl);
	}

	@Override
	public String toString() {
		return "BOOLEAN";
	}

	@Override
	public PrimitiveType commonSupertype(PrimitiveType type) {
		if (isSupertypeOf(type)) {
			return TypeSystem.bool();
		} else {
			return TypeSystem.primitive();
		}
	}

	@Nullable
	@Override
	public BoolType coerceToBool() {
		return this;
	}
}
