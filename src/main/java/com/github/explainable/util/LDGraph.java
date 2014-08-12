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
