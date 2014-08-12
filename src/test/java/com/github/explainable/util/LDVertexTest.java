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

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link LDVertex}.
 */
public class LDVertexTest {
	private LDGraph<String, String> graph = null;

	@Before
	public void setUp() {
		graph = LDGraph.create();
	}

	@After
	public void tearDown() {
		graph = null;
	}

	@Test
	public void testKey() {
		LDVertex<String, String> vertex = graph.addVertex("v1");
		assertEquals("v1", vertex.key());
	}

	@Test
	public void testAnnotate_empty() {
		LDVertex<String, String> vertex = graph.addVertex("v1");
		assertEquals(ImmutableList.<String>of(), vertex.annotations());
	}

	@Test
	public void testAnnotate_nonEmpty() {
		LDVertex<String, String> vertex = graph.addVertex("v1");
		vertex.annotate("A");
		vertex.annotate("B");
		assertEquals(ImmutableList.of("A", "B"), vertex.annotations());
	}

	@Test
	public void testAnnotate_duplicate() {
		LDVertex<String, String> vertex = graph.addVertex("v1");
		vertex.annotate("A");
		vertex.annotate("A");
		assertEquals(ImmutableList.of("A", "A"), vertex.annotations());
	}


	@Test(expected = IllegalArgumentException.class)
	public void testAddEdge_differentGraphs() {
		LDGraph<String, String> graph2 = LDGraph.create();

		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph2.addVertex("v2");

		v1.addEdge(v2);
	}

	@Test
	public void testAddEdge_oneEdge() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph.addVertex("v2");

		LDEdge<String, String> edge = v1.addEdge(v2);
		assertEquals(v1, edge.from());
		assertEquals(v2, edge.to());
		assertEquals(ImmutableList.of(edge), v1.edges());
		assertEquals(ImmutableList.<LDEdge<String, String>>of(), v2.edges());
	}

	@Test
	public void testAddEdge_twoEdges() {
		LDVertex<String, String> v1 = graph.addVertex("V1");
		LDVertex<String, String> v2 = graph.addVertex("v2");
		LDVertex<String, String> v3 = graph.addVertex("v3");

		LDEdge<String, String> edgeToV2 = v1.addEdge(v2);
		LDEdge<String, String> edgeToV3 = v1.addEdge(v3);

		assertEquals(v1, edgeToV2.from());
		assertEquals(v2, edgeToV2.to());
		assertEquals(v1, edgeToV3.from());
		assertEquals(v3, edgeToV3.to());

		assertEquals(ImmutableMultiset.of(edgeToV2, edgeToV3),
				ImmutableMultiset.copyOf(v1.edges()));
		assertEquals(ImmutableList.<LDEdge<String, String>>of(), v2.edges());
		assertEquals(ImmutableList.<LDEdge<String, String>>of(), v3.edges());
	}

	@Test
	public void testAddEdge_self() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDEdge<String, String> edge = v1.addEdge(v1);

		assertEquals(v1, edge.from());
		assertEquals(v1, edge.to());
	}

	@Test
	public void testEquals() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph.addVertex("v2");

		assertEquals(v1, v1);
		assertFalse(v1.equals(v2));
	}

	@Test
	public void testVisitReachableVertices_branching() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph.addVertex("v2");
		LDVertex<String, String> v3 = graph.addVertex("v3");
		v1.addEdge(v2);
		v1.addEdge(v3);

		MockVertexVisitor visitor1 = new MockVertexVisitor(ImmutableList.of(v1, v2, v3));
		v1.visitReachableVertices(visitor1);
		assertTrue(visitor1.isDone());

		MockVertexVisitor visitor2 = new MockVertexVisitor(ImmutableList.of(v2));
		v2.visitReachableVertices(visitor2);
		assertTrue(visitor2.isDone());

		MockVertexVisitor visitor3 = new MockVertexVisitor(ImmutableList.of(v3));
		v3.visitReachableVertices(visitor3);
		assertTrue(visitor3.isDone());
	}

	@Test
	public void testVisitReachableVertices_chain() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph.addVertex("v2");
		LDVertex<String, String> v3 = graph.addVertex("v3");
		v1.addEdge(v2);
		v2.addEdge(v3);

		MockVertexVisitor visitor1 = new MockVertexVisitor(ImmutableList.of(v1, v2, v3));
		v1.visitReachableVertices(visitor1);
		assertTrue(visitor1.isDone());

		MockVertexVisitor visitor2 = new MockVertexVisitor(ImmutableList.of(v2, v3));
		v2.visitReachableVertices(visitor2);
		assertTrue(visitor2.isDone());

		MockVertexVisitor visitor3 = new MockVertexVisitor(ImmutableList.of(v3));
		v3.visitReachableVertices(visitor3);
		assertTrue(visitor3.isDone());
	}

	@Test
	public void testVisitReachableVertices_circle() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph.addVertex("v2");
		LDVertex<String, String> v3 = graph.addVertex("v3");
		v1.addEdge(v2);
		v2.addEdge(v3);
		v3.addEdge(v1);

		MockVertexVisitor visitor1 = new MockVertexVisitor(ImmutableList.of(v1, v2, v3));
		v1.visitReachableVertices(visitor1);
		assertTrue(visitor1.isDone());

		MockVertexVisitor visitor2 = new MockVertexVisitor(ImmutableList.of(v1, v2, v3));
		v2.visitReachableVertices(visitor2);
		assertTrue(visitor2.isDone());

		MockVertexVisitor visitor3 = new MockVertexVisitor(ImmutableList.of(v1, v2, v3));
		v3.visitReachableVertices(visitor3);
		assertTrue(visitor3.isDone());
	}

	@Test
	public void testVisitReachableVertices_selfLoop1() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		v1.addEdge(v1);

		MockVertexVisitor visitor = new MockVertexVisitor(ImmutableList.of(v1));
		v1.visitReachableVertices(visitor);
		assertTrue(visitor.isDone());
	}

	@Test
	public void testVisitReachableVertices_selfLoop2() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		graph.addVertex("v2");
		v1.addEdge(v1);


		MockVertexVisitor visitor = new MockVertexVisitor(ImmutableList.of(v1));
		v1.visitReachableVertices(visitor);
		assertTrue(visitor.isDone());
	}

	@Test
	public void testVisitReachableVertices_order() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph.addVertex("v2");
		LDVertex<String, String> v3 = graph.addVertex("v3");
		v1.addEdge(v2);
		v2.addEdge(v3);

		final List<LDVertex<String, String>> remaining = Lists.newArrayList();
		remaining.add(v1);
		remaining.add(v2);
		remaining.add(v3);

		LDVertexVisitor<String, String> visitor = new LDVertexVisitor<String, String>() {
			@Override
			public void visit(LDVertex<String, String> vertex) {
				if (remaining.get(0) == vertex) {
					remaining.remove(0);
				} else {
					throw new AssertionError();
				}
			}
		};
		v1.visitReachableVertices(visitor);
		assertTrue(remaining.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCanReach_differentGraphs() {
		LDGraph<String, String> graph2 = LDGraph.create();

		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph2.addVertex("v2");

		v1.canReach(v2);
	}

	@Test
	public void testCanReach_branching() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph.addVertex("v2");
		LDVertex<String, String> v3 = graph.addVertex("v3");
		v1.addEdge(v2);
		v1.addEdge(v3);

		assertTrue(v1.canReach(v1));
		assertTrue(v1.canReach(v2));
		assertTrue(v1.canReach(v3));

		assertFalse(v2.canReach(v1));
		assertTrue(v2.canReach(v2));
		assertFalse(v2.canReach(v3));

		assertFalse(v3.canReach(v1));
		assertFalse(v3.canReach(v2));
		assertTrue(v3.canReach(v3));
	}

	@Test
	public void testCanReach_chain() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph.addVertex("v2");
		LDVertex<String, String> v3 = graph.addVertex("v3");
		v1.addEdge(v2);
		v2.addEdge(v3);

		assertTrue(v1.canReach(v1));
		assertTrue(v1.canReach(v2));
		assertTrue(v1.canReach(v3));

		assertFalse(v2.canReach(v1));
		assertTrue(v2.canReach(v2));
		assertTrue(v2.canReach(v3));

		assertFalse(v3.canReach(v1));
		assertFalse(v3.canReach(v2));
		assertTrue(v3.canReach(v3));
	}

	@Test
	public void testCanReach_circle() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph.addVertex("v2");
		LDVertex<String, String> v3 = graph.addVertex("v3");
		v1.addEdge(v2);
		v2.addEdge(v3);
		v3.addEdge(v1);

		assertTrue(v1.canReach(v1));
		assertTrue(v1.canReach(v2));
		assertTrue(v1.canReach(v3));

		assertTrue(v2.canReach(v1));
		assertTrue(v2.canReach(v2));
		assertTrue(v2.canReach(v3));

		assertTrue(v2.canReach(v1));
		assertTrue(v2.canReach(v2));
		assertTrue(v2.canReach(v3));
	}

	@Test
	public void testCanReach_selfLoop1() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		v1.addEdge(v1);

		assertTrue(v1.canReach(v1));
	}

	@Test
	public void testCanReach_selfLoop2() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph.addVertex("v2");
		v1.addEdge(v1);

		assertFalse(v1.canReach(v2));
	}

	private static class MockVertexVisitor implements LDVertexVisitor<String, String> {
		private final Multiset<LDVertex<String, String>> remaining;

		MockVertexVisitor(ImmutableList<LDVertex<String, String>> expected) {
			this.remaining = HashMultiset.create(expected);
		}

		@Override
		public void visit(LDVertex<String, String> vertex) {
			assertTrue(remaining.remove(vertex));
		}

		boolean isDone() {
			return remaining.isEmpty();
		}
	}
}
