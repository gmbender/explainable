/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Gabriel Bender
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.explainable.sql.type;

import javax.annotation.Nullable;

/**
 * Class that represents the type of the value {@code NULL}, which as subtype of all other primitive
 * types.
 */
final class PrimitiveBottomTypeImpl extends AbstractPrimitiveType implements PrimitiveBottomType {
	@Override
	public boolean isSupertypeOf(Type type) {
		return (type instanceof PrimitiveBottomType);
	}

	@Override
	public int hashCode() {
		return -1954505528; // Randomly generated constant
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof PrimitiveBottomTypeImpl);
	}

	@Override
	public String toString() {
		return "NULL";
	}

	@Override
	public PrimitiveType commonSupertype(PrimitiveType type) {
		// PrimitiveBottomType is a bottom type for primitives, which means that it is a subtype of every
		// primitive type (including itself, if you want to be pedantic).
		return type;
	}

	@Nullable
	@Override
	public BoolType coerceToBool() {
		return this;
	}

	@Nullable
	@Override
	public NumericType coerceToNumeric() {
		return this;
	}

	@Nullable
	@Override
	public StringType coerceToString() {
		return this;
	}

	@Nullable
	@Override
	public PrimitiveType unifyWith(PrimitiveType type) {
		return this;
	}
}
