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

package com.github.explainable.util;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

public final class UnionFindNode<V> {
	@Nullable
	private V value;

	@Nullable
	private UnionFindNode<V> parent;

	private int rank;

	private UnionFindNode(V value) {
		this.value = Preconditions.checkNotNull(value);
		this.parent = null;
		this.rank = 0;
	}

	public static <V> UnionFindNode<V> create(V value) {
		return new UnionFindNode<V>(value);
	}

	public V get() {
		return root().value;
	}

	public void set(V value) {
		root().value = value;
	}

	public UnionFindNode<V> root() {
		if (parent == null) {
			return this;
		} else {
			if (parent.parent != null) {
				parent = parent.root();
			}

			return parent;
		}
	}

	public boolean mergeWith(UnionFindNode<V> other) {
		UnionFindNode<V> lhs = root();
		UnionFindNode<V> rhs = other.root();

		if (lhs.value != null && rhs.value != null && !lhs.value.equals(rhs.value)) {
			// The nodes can't be unified because they're bound to different values.
			return false;
		}

		if (lhs == rhs) {
			// The nodes are already unified.
			return true;
		}

		V mergedValue = (lhs.value != null)
				? lhs.value
				: rhs.value;

		// Merge the smaller (lower-rank) component into the larger (higher-rank) one.
		if (lhs.rank < rhs.rank) {
			lhs.parent = rhs;
			rhs.value = mergedValue;
		} else if (lhs.rank > rhs.rank) {
			rhs.parent = lhs;
			lhs.value = mergedValue;
		} else {
			// lhs.rank == rhs.rank
			lhs.parent = rhs;
			rhs.value = mergedValue;
			rhs.rank++;
		}

		return true;
	}

	@Nullable
	@Override
	public String toString() {
		return (parent == null)
				? hashCode() + "[" + value + "]"
				: hashCode() + " => " + parent;
	}
}
