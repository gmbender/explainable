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

package com.github.explainable.corelang;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.Map;

abstract class ExtendableTermMap implements TermMap {
	private final Map<Term, Term> forwardMap;

	private final Map<Term, Term> backwardMap;

	ExtendableTermMap() {
		this.forwardMap = Maps.newHashMap();
		this.backwardMap = Maps.newHashMap();
	}

	ExtendableTermMap(ExtendableTermMap original) {
		this.forwardMap = Maps.newHashMap(original.forwardMap);
		this.backwardMap = Maps.newHashMap(original.backwardMap);
	}

	/**
	 * Subclasses should override this method to determine whether the current map can be extended to
	 * send {@code from} to {@code to}. For internal use only.
	 */
	abstract boolean canExtend(Term source, Term target, @Nullable Term oldSource);

	/**
	 * Subclasses should override this method, which will be called immediately after the current map
	 * has been extended to send {@code from} to {@code to}. For internal use only.
	 */
	abstract void didExtend(Term from, Term to);

	/**
	 * Create a copy of the current object. This is similar to clone(), but without the tricky default
	 * behaviors.
	 */
	abstract ExtendableTermMap copy();

	/**
	 * Extend the current map by adding (at most) one new element to its domain. This operation fails
	 * atomically.
	 *
	 * @param from A term that will be added to the map's domain
	 * @param to A term that will be added to the map's image
	 * @return {@code true} if the operation succeeded, or {@code false} otherwise.
	 */
	private boolean augment(Term from, Term to) {
		if (forwardMap.containsKey(from)) {
			return forwardMap.get(from).equals(to);
		} else {
			Term oldPreImage = backwardMap.get(to);

			if (canExtend(from, to, oldPreImage)) {
				forwardMap.put(from, to);
				backwardMap.put(to, from);
				didExtend(from, to);
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Extend the current map by sending each term in {@code from} to the corresponding term in {@code
	 * to}. This method will only succeed if {@code from} and {@code to} are atoms on the same
	 * relation. The current object will not be modified. The new object will be constructed using
	 * {@link #copy()} method and then augmented zero or more times.
	 *
	 * @param from the atom whose terms will extend the map's domain
	 * @param to the atom whose terms will extend the map's range
	 * @return the extended map, or {@code null} if no suitable map exists
	 */
	@Nullable
	ExtendableTermMap extend(Atom from, Atom to) {
		if (!from.relation().equals(to.relation())) {
			return null;
		}

		Preconditions.checkArgument(from.arguments().size() == to.arguments().size());

		ExtendableTermMap result = copy();
		for (int i = 0; i < from.arguments().size(); i++) {
			if (!result.augment(from.arguments().get(i), to.arguments().get(i))) {
				return null;
			}
		}

		if (!result.augment(from.getCopyVariable(), to.getCopyVariable())) {
			return null;
		}

		return result;
	}

	/**
	 * Apply the current map to all the terms in {@code fromTerms}, and return the result.
	 *
	 * @return the modified list of terms
	 */
	@Override
	public final Term apply(Term from) {
		Term to = forwardMap.get(from);
		return (to != null) ? to : from;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("forwardMap", forwardMap)
				.add("backwardMap", backwardMap)
				.toString();
	}
}
