package com.github.explainable.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link LDEdge}.
 */
public class LDEdgeTest {
	private LDGraph<String, String> graph;

	@Before
	public void setUp() {
		graph = LDGraph.create();
	}

	@After
	public void tearDown() {
		graph = null;
	}

	@Test
	public void testFrom() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph.addVertex("v2");
		LDEdge<String, String> edge = new LDEdge<String, String>(v1, v2);

		assertEquals(v1, edge.from());
	}

	@Test
	public void testTo() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph.addVertex("v2");
		LDEdge<String, String> edge = new LDEdge<String, String>(v1, v2);

		assertEquals(v2, edge.to());
	}
}
