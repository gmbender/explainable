package com.github.explainable.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

/**
 * Unit tests for {@link LDGraph}.
 */
public class LDGraphTest {
	private LDGraph<String, String> graph;

	@Before
	public void setUp() {
		graph = LDGraph.create();
	}

	@Test
	public void testAddVertex1() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		LDVertex<String, String> v2 = graph.addVertex("V2");

		assertNotNull(v1);
		assertNotNull(v2);
		assertNotSame(v1, v2);
	}

	@Test
	public void testAddVertex2() {
		LDVertex<String, String> v1 = graph.addVertex("v1");
		assertEquals("v1", v1.key());
	}
}
