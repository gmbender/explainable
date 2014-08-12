package com.github.explainable.sql.constraint;

import com.github.explainable.sql.table.BaseColumn;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 10/28/13 Time: 1:51 PM To change this template
 * use File | Settings | File Templates.
 */
public final class BaseColumnArg extends EqualityArg {
	private final BaseColumn column;

	private BaseColumnArg(BaseColumn column) {
		this.column = Preconditions.checkNotNull(column);
	}

	public static BaseColumnArg of(BaseColumn column) {
		return new BaseColumnArg(column);
	}

	public BaseColumn column() {
		return column;
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
		return column.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (!(other instanceof BaseColumnArg)) {
			return false;
		}

		BaseColumnArg otherColumnArg = (BaseColumnArg) other;
		return column.equals(otherColumnArg.column);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("column", column)
				.toString();
	}
}
