package com.github.explainable.util;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Class that represents an edge in a {@link LDGraph}. Each edge is labeled with zero or more labels
 * of type {@code L}.
 */
public final class LDEdge<V, L> {
	private final LDVertex<V, L> from;

	private final LDVertex<V, L> to;

	private final List<L> labels;

	LDEdge(LDVertex<V, L> from, LDVertex<V, L> to) {
		this.from = Preconditions.checkNotNull(from);
		this.to = Preconditions.checkNotNull(to);
		this.labels = Lists.newArrayList();
	}

	public LDVertex<V, L> from() {
		return from;
	}

	public LDVertex<V, L> to() {
		return to;
	}

	@Override
	public String toString() {
		return from.hashCode() + " -> " + to.hashCode() + " (" + Joiner.on(", ").join(labels) + ")";
	}
}
