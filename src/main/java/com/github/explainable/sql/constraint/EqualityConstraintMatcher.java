package com.github.explainable.sql.constraint;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 10/28/13 Time: 1:38 PM To change this template
 * use File | Settings | File Templates.
 */
public interface EqualityConstraintMatcher {
	void match(ConstantArg left, ConstantArg right);

	void match(ConstantArg left, BaseColumnArg right);

	void match(BaseColumnArg left, ConstantArg right);

	void match(BaseColumnArg left, BaseColumnArg right);
}
