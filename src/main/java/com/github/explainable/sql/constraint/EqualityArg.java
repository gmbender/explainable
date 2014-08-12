package com.github.explainable.sql.constraint;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 10/28/13 Time: 1:45 PM To change this template
 * use File | Settings | File Templates.
 */
public abstract class EqualityArg {
	EqualityArg() {
	}

	abstract void matchLeft(EqualityConstraintMatcher matcher, EqualityArg right);

	abstract void matchRight(EqualityConstraintMatcher matcher, ConstantArg left);

	abstract void matchRight(EqualityConstraintMatcher matcher, BaseColumnArg left);
}
