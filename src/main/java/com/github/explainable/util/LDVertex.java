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

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing a vertex in a {@link LDGraph}.
 */
public final class LDVertex<V, L> {
	// The key associated with the current vertex.
	private final V key;

	// The graph that contains the current vertex.
	private final LDGraph<V, L> graph;

	// A list of directed edges which come out of the current vertex.
	private final List<LDEdge<V, L>> edges;

	private final List<L> annotations;

	LDVertex(V key, LDGraph<V, L> graph) {
		this.key = Preconditions.checkNotNull(key);
		this.graph = Preconditions.checkNotNull(graph);
		this.edges = Lists.newArrayList();
		this.annotations = Lists.newArrayList();
	}

	@Nullable
	public V key() {
		return key;
	}

	/**
	 * Get a list of the edges from the current vertex. WARNING: Some edges may be repeated.
	 */
	ImmutableList<LDEdge<V, L>> edges() {
		return ImmutableList.copyOf(edges);
	}

	/**
	 * Add an edge to the graph from the current vertex to {@code target}.
	 *
	 * @param target the vertex we want to add an edge to
	 * @return an edge from the current vertex to {@code target}
	 */
	public LDEdge<V, L> addEdge(LDVertex<V, L> target) {
		Preconditions.checkNotNull(target);
		Preconditions.checkArgument(graph == target.graph);

		LDEdge<V, L> result = new LDEdge<V, L>(this, target);
		edges.add(result);
		return result;
	}

	/**
	 * Perform a pre-order traversal of the graph, starting at the current vertex. The visitor's {@link
	 * LDVertexVisitor#visit} method will be called exactly once for each reachable vertex.
	 *
	 * @param visitor a visitor whose {@link LDVertexVisitor#visit} method will be invoked on each
	 * reachable vertex
	 */
	public void visitReachableVertices(LDVertexVisitor<V, L> visitor) {
		visitReachableVerticesImpl(visitor, Sets.<LDVertex<V, L>>newHashSet());
	}

	private void visitReachableVerticesImpl(
			LDVertexVisitor<V, L> visitor,
			Set<LDVertex<V, L>> visitedVertices) {
		if (!visitedVertices.contains(this)) {
			visitor.visit(this);
			visitedVertices.add(this);

			for (LDEdge<V, L> edge : edges) {
				edge.to().visitReachableVerticesImpl(visitor, visitedVertices);
			}
		}
	}

	/**
	 * Determine whether there is a direct path from the current vertex to {@code target}.
	 *
	 * @param target the vertex we want to reach
	 * @return {@code true} if a directed path exists, and {@code false} otherwise
	 */
	public boolean canReach(LDVertex<V, L> target) {
		Preconditions.checkNotNull(target);
		Preconditions.checkArgument(graph == target.graph);

		return canReachImpl(target, Sets.<LDVertex<V, L>>newHashSet());
	}

	private boolean canReachImpl(LDVertex<V, L> target, HashSet<LDVertex<V, L>> visitedVertices) {
		if (this.equals(target)) {
			return true;
		}

		if (!visitedVertices.contains(this)) {
			visitedVertices.add(this);
			for (LDEdge<V, L> edge : edges) {
				if (edge.to().canReachImpl(target, visitedVertices)) {
					return true;
				}
			}
		}

		return false;
	}

	public void annotate(L annotation) {
		annotations.add(annotation);
	}

	public ImmutableList<L> annotations() {
		return ImmutableList.copyOf(annotations);
	}

	@Override
	public String toString() {
		List<String> edgeStrings = Lists.newArrayList();
		for (LDEdge<V, L> edge : edges) {
			edgeStrings.add(String.valueOf(edge.to().hashCode()));
		}

		return "{ hash = " + hashCode()
				+ ", key = " + key
				+ ", edge " + Joiner.on(", edge ").join(edgeStrings)
				+ " }";
	}
}
