package com.github.explainable.corelang;

public final class MultisetVariable extends Term {
	private static final int UNIFICATION_PRIORITY = 2;

	MultisetVariable() {
	}

	@Override
	int unificationPriority() {
		return UNIFICATION_PRIORITY;
	}

	@Override
	public Term unifyWithSameType(Term term) {
		if (!(term instanceof MultisetVariable)) {
			throw new IllegalArgumentException("unifyWithSameType: Not a constant: " + term);
		}

		// Both variables are structurally equivalent; we can choose one arbitrarily.
		return this;
	}

	@Override
	boolean canSpecialize(Term target, TermType oldMultisetImageType) {
		return (oldMultisetImageType == TermType.NONE || oldMultisetImageType == target.type())
				&& target.type().isExistential();
	}

	@Override
	boolean canSpecializeFromBoth(Term source, Term oldSource) {
		return false;
	}

	@Override
	public boolean canFold(Term target) {
		return (target.type() == TermType.MULTISET_VARIABLE);
	}

	@Override
	boolean canFoldFromBoth(Term source, Term oldSource) {
		return true;
	}

	@Override
	public TermType type() {
		return TermType.MULTISET_VARIABLE;
	}

	@Override
	public String toString() {
		return "Multiset[" + hashCode() + "]";
	}
}
