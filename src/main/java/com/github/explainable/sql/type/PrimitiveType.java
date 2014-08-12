package com.github.explainable.sql.type;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/23/13 Time: 9:41 AM To change this template use
 * File | Settings | File Templates.
 */
public interface PrimitiveType extends Type {
	/**
	 * Return the most specific type that is a supertype of both the current object and the argument
	 * type.
	 *
	 * @param type The type that we want to compare against
	 * @return The result type
	 */
	PrimitiveType commonSupertype(PrimitiveType type);

	/**
	 * Return the most general type that is a subtype of both the current object and the argument
	 * type.
	 *
	 * @param type The type of that we want to unify with
	 * @return The result type, or {@code null} if no appropriate type exists
	 */
	@Nullable
	PrimitiveType unifyWith(PrimitiveType type);
}
