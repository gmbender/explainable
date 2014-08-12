package com.github.explainable.corelang;

import javax.annotation.Nullable;

public final class DistVariable extends Term {
	private static final int UNIFICATION_PRIORITY = 1;

	DistVariable() {
	}

	@Override
	int unificationPriority() {
		return UNIFICATION_PRIORITY;
	}

	@Nullable
	@Override
	public Term unifyWithSameType(Term term) {
		if (!(term instanceof DistVariable)) {
			throw new IllegalArgumentException("unifyWithSameType: Not a constant: " + term);
		}

		// Both variables are structurally equivalent; we can choose one arbitrarily.
		return this;
	}

	@Override
	boolean canSpecialize(Term target, TermType oldMultisetImageType) {
		return true;
	}

	@Override
	boolean canSpecializeFromBoth(Term source, Term oldSource) {
		return true;
	}

	@Override
	public boolean canFold(Term target) {
		return (target.type() == TermType.DIST_VARIABLE);
	}

	@Override
	boolean canFoldFromBoth(Term source, Term oldSource) {
		TermType sourceType = source.type();
		TermType oldSourceType = oldSource.type();
		return (sourceType != TermType.DIST_VARIABLE) || (oldSourceType != TermType.DIST_VARIABLE);
	}

	@Override
	public TermType type() {
		return TermType.DIST_VARIABLE;
	}

	@Override
	public String toString() {
		return "Dist[" + hashCode() + "]";
	}
}
