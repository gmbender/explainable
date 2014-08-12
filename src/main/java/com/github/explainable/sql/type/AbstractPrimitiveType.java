package com.github.explainable.sql.type;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/23/13 Time: 2:53 PM To change this template use
 * File | Settings | File Templates.
 */
abstract class AbstractPrimitiveType extends AbstractType implements PrimitiveType {
	@Nullable
	@Override
	public PrimitiveType coerceToPrimitive() {
		return this;
	}

	@Nullable
	@Override
	public PrimitiveType unifyWith(PrimitiveType type) {
		// This implementation is currently fairly fragile, but is good enough for our current needs.
		// It assumes that given two primitive types, either one must be a supertype of the other
		// (e.g., Primitive is a supertype of Bool, and Bool is a supertype of Null), or else the
		// unifier of the two types does not exist (e.g., Numeric and String).
		if (isSupertypeOf(type)) {
			return type;
		} else if (type.isSupertypeOf(this)) {
			return this;
		} else {
			// TODO: Should we replace null with PrimitiveBottomType?
			return null;
		}
	}
}
