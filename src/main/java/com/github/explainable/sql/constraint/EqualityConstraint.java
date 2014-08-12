package com.github.explainable.sql.constraint;

import com.google.common.base.Preconditions;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 10/28/13 Time: 1:36 PM To change this template
 * use File | Settings | File Templates.
 */
public final class EqualityConstraint {
	private final EqualityArg left;

	private final EqualityArg right;

	private EqualityConstraint(EqualityArg left, EqualityArg right) {
		this.left = Preconditions.checkNotNull(left);
		this.right = Preconditions.checkNotNull(right);
	}

	public static EqualityConstraint create(EqualityArg left, EqualityArg right) {
		return new EqualityConstraint(left, right);
	}

	public EqualityArg left() {
		return left;
	}

	public EqualityArg right() {
		return right;
	}

	public void match(EqualityConstraintMatcher matcher) {
		left.matchLeft(matcher, right);
	}

	@Override
	public int hashCode() {
		return left.hashCode() + 17 * right.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (!(other instanceof EqualityConstraint)) {
			return false;
		}

		EqualityConstraint otherConstraint = (EqualityConstraint) other;
		return left.equals(otherConstraint.left)
				&& right.equals(otherConstraint.right);
	}

	@Override
	public String toString() {
		return "Eq(" + left + ", " + right + ")";
	}
}
