package com.github.explainable.sql.type;

import javax.annotation.Nullable;

/**
 * Class that represents the type of the value {@code NULL}, which as subtype of all other primitive
 * types.
 */
final class PrimitiveBottomTypeImpl extends AbstractPrimitiveType implements PrimitiveBottomType {
	@Override
	public boolean isSupertypeOf(Type type) {
		return (type instanceof PrimitiveBottomType);
	}

	@Override
	public int hashCode() {
		return -1954505528; // Randomly generated constant
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof PrimitiveBottomTypeImpl);
	}

	@Override
	public String toString() {
		return "NULL";
	}

	@Override
	public PrimitiveType commonSupertype(PrimitiveType type) {
		// PrimitiveBottomType is a bottom type for primitives, which means that it is a subtype of every
		// primitive type (including itself, if you want to be pedantic).
		return type;
	}

	@Nullable
	@Override
	public BoolType coerceToBool() {
		return this;
	}

	@Nullable
	@Override
	public NumericType coerceToNumeric() {
		return this;
	}

	@Nullable
	@Override
	public StringType coerceToString() {
		return this;
	}

	@Nullable
	@Override
	public PrimitiveType unifyWith(PrimitiveType type) {
		return this;
	}
}
