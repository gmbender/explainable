package com.github.explainable.corelang;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

/**
 * Abstract class that represents a constant in a conjunctive query.
 */
public final class Constant extends Term {
	private static final int UNIFICATION_PRIORITY = 0;

	private final Object value;

	Constant(Object value) {
		this.value = Preconditions.checkNotNull(value);
	}

	@Override
	int unificationPriority() {
		return UNIFICATION_PRIORITY;
	}

	@Nullable
	@Override
	public Term unifyWithSameType(Term term) {
		if (!(term instanceof Constant)) {
			throw new IllegalArgumentException("unifyWithSameType: Not a constant: " + term);
		}

		return value.equals(((Constant) term).value) ? this : null;
	}

	@Override
	boolean canSpecialize(Term target, TermType oldMultisetImageType) {
		return equals(target);
	}

	@Override
	boolean canSpecializeFromBoth(Term source, Term oldSource) {
		return true;
	}

	@Override
	public boolean canFold(Term target) {
		return equals(target);
	}

	@Override
	boolean canFoldFromBoth(Term source, Term oldSource) {
		return true;
	}

	@Override
	public TermType type() {
		return TermType.CONSTANT;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * Two constants are equal if and only if their corresponding values are equal.
	 */
	@Override
	public boolean equals(Object other) {
		return (other instanceof Constant) && value.equals(((Constant) other).value);
	}

	@Override
	public String toString() {
		return (value instanceof String) ? ("\'" + value + "\'") : value.toString();
	}
}
