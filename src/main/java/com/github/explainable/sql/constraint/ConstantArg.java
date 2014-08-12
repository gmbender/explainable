package com.github.explainable.sql.constraint;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 10/28/13 Time: 1:42 PM To change this template
 * use File | Settings | File Templates.
 */
public final class ConstantArg extends EqualityArg {
	private final Object value;

	private ConstantArg(Object value) {
		this.value = Preconditions.checkNotNull(value);
	}

	public static ConstantArg of(Object value) {
		return new ConstantArg(value);
	}

	public Object value() {
		return value;
	}

	@Override
	void matchLeft(EqualityConstraintMatcher matcher, EqualityArg right) {
		right.matchRight(matcher, this);
	}

	@Override
	void matchRight(EqualityConstraintMatcher matcher, ConstantArg left) {
		matcher.match(left, this);
	}

	@Override
	void matchRight(EqualityConstraintMatcher matcher, BaseColumnArg left) {
		matcher.match(left, this);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (!(other instanceof ConstantArg)) {
			return false;
		}

		ConstantArg otherConstantArg = (ConstantArg) other;
		return value.equals(otherConstantArg.value);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("value", value)
				.toString();
	}
}
