package com.github.explainable.corelang;

import javax.annotation.Nullable;

public final class SetVariable extends Term {
	private static final int UNIFICATION_PRIORITY = 3;

	SetVariable() {
	}

	@Override
	int unificationPriority() {
		return UNIFICATION_PRIORITY;
	}

	@Nullable
	@Override
	public Term unifyWithSameType(Term term) {
		if (!(term instanceof SetVariable)) {
			throw new IllegalArgumentException("unifyWithSameType: Not a constant: " + term);
		}

		// Both variables are structurally equivalent; we can choose one arbitrarily.
		return this;
	}

	@Override
	boolean canSpecialize(Term target, TermType oldMultisetImageType) {
		return (target.type() == TermType.SET_VARIABLE);
	}

	@Override
	boolean canSpecializeFromBoth(Term source, Term oldSource) {
		return false;
	}

	@Override
	public boolean canFold(Term target) {
		return true;
	}

	@Override
	boolean canFoldFromBoth(Term source, Term oldSource) {
		return true;
	}

	@Override
	public TermType type() {
		return TermType.SET_VARIABLE;
	}

	@Override
	public String toString() {
		return "Set[" + hashCode() + "]";
	}
}
