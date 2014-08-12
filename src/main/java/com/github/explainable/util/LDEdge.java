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
