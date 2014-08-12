package com.github.explainable.sql.type;

/**
 * Abstract supertype of all other primitive (i.e., non-table-valued) types.
 */
final class PrimitiveTypeImpl extends AbstractPrimitiveType {
	@Override
	public boolean isSupertypeOf(Type type) {
		return (type instanceof PrimitiveType);
	}

	@Override
	public int hashCode() {
		return 507640536; // Randomly generated constant
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof PrimitiveTypeImpl);
	}

	@Override
	public String toString() {
		return "PRIMITIVE";
	}

	@Override
	public PrimitiveType commonSupertype(PrimitiveType type) {
		return TypeSystem.primitive();
	}
}
