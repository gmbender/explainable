package com.github.explainable.util;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link UnionFindNode}.
 */
public final class UnionFindNodeTest {
	@Test
	public void testGetValue() {
		UnionFindNode<String> node = UnionFindNode.create("hello");
		assertEquals("hello", node.get());
	}

	@Test
	public void testSetValue() {
		UnionFindNode<String> node = UnionFindNode.create("hello");
		node.set("goodbye");
		assertEquals("goodbye", node.get());
	}

	@Test
	public void testSetValue_withParent1() {
		UnionFindNode<String> node1 = UnionFindNode.create("a");
		UnionFindNode<String> node2 = UnionFindNode.create("a");
		node1.mergeWith(node2);

		node1.set("my value");
		assertEquals("my value", node1.get());
		assertEquals("my value", node2.get());
	}

	@Test
	public void testSetValue_withParent2() {
		UnionFindNode<String> node1 = UnionFindNode.create("a");
		UnionFindNode<String> node2 = UnionFindNode.create("a");
		node1.mergeWith(node2);

		node2.set("my value");
		assertEquals("my value", node1.get());
		assertEquals("my value", node2.get());
	}

	@Test
	public void testGetRoot_singleton() {
		UnionFindNode<String> node = UnionFindNode.create("a");
		assertEquals(node, node.root());
		assertEquals("a", node.root().get());
	}

	@Test
	public void testMergeWith_twoWay() {
		UnionFindNode<String> node1 = UnionFindNode.create("a");
		UnionFindNode<String> node2 = UnionFindNode.create("a");

		assertTrue(node1.mergeWith(node2));

		assertSame(node1.root(), node2.root());
		assertTrue(ImmutableList.of(node1, node2).contains(node1.root()));
		assertEquals("a", node1.root().get());
	}

	@Test
	public void testMergeWith_threeWay1() {
		UnionFindNode<String> node1 = UnionFindNode.create("a");
		UnionFindNode<String> node2 = UnionFindNode.create("a");
		UnionFindNode<String> node3 = UnionFindNode.create("a");

		assertTrue(node1.mergeWith(node2));
		assertTrue(node2.mergeWith(node3));

		assertSame(node1.root(), node2.root());
		assertSame(node1.root(), node3.root());
		assertTrue(ImmutableList.of(node1, node2, node3).contains(node1.root()));
		assertEquals("a", node1.root().get());
	}

	@Test
	public void testMergeWith_threeWay2() {
		UnionFindNode<String> node1 = UnionFindNode.create("a");
		UnionFindNode<String> node2 = UnionFindNode.create("a");
		UnionFindNode<String> node3 = UnionFindNode.create("a");

		assertTrue(node1.mergeWith(node2));
		assertTrue(node1.mergeWith(node3));

		assertSame(node1.root(), node2.root());
		assertSame(node1.root(), node3.root());
		assertTrue(ImmutableList.of(node1, node2, node3).contains(node1.root()));
		assertEquals("a", node1.root().get());
	}

	@Test
	public void testGetRoot_withConstant() {
		assertEquals("a", UnionFindNode.create("a").root().get());
	}

	@Test
	public void testGetRoot_twoWayWithOneConstant1() {
		UnionFindNode<String> node1 = UnionFindNode.create("a");
		UnionFindNode<String> node2 = UnionFindNode.create("a");

		assertTrue(node1.mergeWith(node2));

		assertEquals("a", node1.get());
		assertEquals("a", node2.get());
	}

	@Test
	public void testGetRoot_twoWayWithOneConstant2() {
		UnionFindNode<String> node1 = UnionFindNode.create("a");
		UnionFindNode<String> node2 = UnionFindNode.create("a");

		assertTrue(node1.mergeWith(node2));

		assertSame(node1.root(), node2.root());
		assertEquals("a", node1.get());
		assertEquals("a", node2.get());
	}

	@Test
	public void testGetRoot_twoWayWithSameConstant() {
		UnionFindNode<String> node1 = UnionFindNode.create("a");
		UnionFindNode<String> node2 = UnionFindNode.create("a");

		assertTrue(node1.mergeWith(node2));

		assertSame(node1.root(), node2.root());
		assertEquals("a", node1.get());
		assertEquals("a", node2.get());
	}

	@Test
	public void testGetRoot_twoWayWithDifferentConstants() {
		UnionFindNode<String> node1 = UnionFindNode.create("a");
		UnionFindNode<String> node2 = UnionFindNode.create("b");

		assertFalse(node1.mergeWith(node2));

		assertEquals(node1, node1.root());
		assertEquals(node2, node2.root());
		assertEquals("a", node1.get());
		assertEquals("b", node2.get());
	}

	@Test
	public void testUnifyAndGetRoot_self() {
		UnionFindNode<String> node = UnionFindNode.create("a");
		assertTrue(node.mergeWith(node));
		assertEquals(node, node.root());
	}
}
