package com.github.explainable.corelang;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

/**
 * Abstract class that represents a term in a conjunctive query.
 */
public abstract class Term {
	Term() {
		// Can only be extended by classes in the same package.
	}

	/**
	 * Compute a term with the property that if {@code C = A.unifyWith(B)} then {@code Q(...) :- R(A,
	 * B), A = B} is equivalent to {@code Q(...) :- R(C, C)}. This method is permitted to return one of
	 * its inputs.
	 *
	 * @param term the term we should unify the current object with
	 * @return the unified term {@code C}, or {@code null} if none exists
	 */
	@Nullable
	public final Term unifyWith(Term term) {
		Preconditions.checkNotNull(term);

		int thisPriority = unificationPriority();
		int termPriority = term.unificationPriority();

		Preconditions.checkState(
				(thisPriority == termPriority) == (getClass() == term.getClass()),
				"Two terms should only have the same priority if they have the same type");

		if (thisPriority == termPriority) {
			// Let the subclass manage unification.
			return unifyWithSameType(term);
		} else {
			// Select whichever term has the lower priority.
			return (unificationPriority() < term.unificationPriority()) ? this : term;
		}
	}

	/**
	 * Internally, each subclass of {@code Term} has a different (fixed) priority, which can be an
	 * arbitrary integer. When two terms of different types are unified, the one with the lower
	 * priority should be returned by the {@link #unifyWith} method. When two terms of the same type
	 * are unified, the result is determined by the {@link #unifyWithSameType} method below.
	 */
	abstract int unificationPriority();

	/**
	 * Helper method for {@link #unifyWith} that is responsible for unifying different terms of the
	 * same type. Must be implemented by each subclass.
	 */
	@Nullable
	abstract Term unifyWithSameType(Term term);

	/**
	 * Determine whether a {@link Specialization} can map the current term to {@code target}.
	 */
	abstract boolean canSpecialize(Term target, TermType oldMultisetImageType);

	/**
	 * Determine whether a {@link Specialization} can map both {@code source} and {@code oldSource} to
	 * the current term.
	 */
	abstract boolean canSpecializeFromBoth(Term source, Term oldSource);

	/**
	 * Determine whether a {@link Homomorphism} can map the current term to {@code target}.
	 */
	abstract boolean canFold(Term target);

	/**
	 * Determine whether a {@link Homomorphism} can map both {@code source} and {@code oldSource} to
	 * the current term.
	 */
	abstract boolean canFoldFromBoth(Term source, Term oldSource);

	/**
	 * Get the {@link TermType} of the current term.
	 */
	public abstract TermType type();
}
