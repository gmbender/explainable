package com.github.explainable.corelang;

import com.google.common.base.Preconditions;

/**
 * Static factory for creating new {@link Term}s.
 */
public final class Terms {
	private static int counter = 0;

	private Terms() {
		throw new UnsupportedOperationException("Can't instantiated Terms");
	}

	/**
	 * Create a fresh distinguished variable.
	 */
	public static DistVariable dist() {
		return new DistVariable();
	}

	/**
	 * Create a fresh multiset-existential variable.
	 */
	public static MultisetVariable multiset() {
		return new MultisetVariable();
	}

	/**
	 * Create a fresh set-existential variable.
	 */
	public static SetVariable set() {
		return new SetVariable();
	}

	/**
	 * Create a constant with the specified value.
	 */
	public static Constant constant(Object value) {
		return new Constant(value);
	}

	/**
	 * Create a new term with the specified type.
	 */
	public static Term variableWithType(TermType type) {
		Preconditions.checkNotNull(type);
		Term result;

		switch (type) {
			case DIST_VARIABLE:
				result = dist();
				break;
			case MULTISET_VARIABLE:
				result = multiset();
				break;
			case SET_VARIABLE:
				result = set();
				break;
			case CONSTANT: // Fall-through
			case NONE: // Fall-through
			default:
				throw new IllegalArgumentException("Unknown term type: " + type);
		}

		return result;
	}
}
