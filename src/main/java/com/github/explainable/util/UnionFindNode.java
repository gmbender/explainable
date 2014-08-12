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
