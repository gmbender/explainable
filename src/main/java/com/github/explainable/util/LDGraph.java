package com.github.explainable.util;

/**
 * Class representing a directed graph with self-loops. Each vertex can optionally be tagged with an
 * object of type {@code V}, and each edge can be labeled with zero or more objects of type {@code
 * L}. {@code LDGraph} is short for labeled digraph.
 */
public final class LDGraph<V, L> {
	private LDGraph() {
	}

	public static <V, L> LDGraph<V, L> create() {
		return new LDGraph<V, L>();
	}

	/**
	 * If the graph does not already contain a vertex with key {@code V} then this method will add a
	 * new vertex with the specified {@code key} to the graph.
	 *
	 * @param key a non-null key associated with the vertex
	 * @return the vertex associated with the specified {@code key}
	 */
	public LDVertex<V, L> addVertex(V key) {
		return new LDVertex<V, L>(key, this);
	}
}
