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

import org.junit.Test;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for {@link ProperArrayListStack}.
 */
public final class ProperArrayListStackTest {
	@Test
	public void testCreate() {
		ProperStack<Object> stack1 = ProperArrayListStack.create();
		ProperStack<Object> stack2 = ProperArrayListStack.create();

		assertNotSame(stack1, stack2);
	}

	@Test
	public void testCreate_emptyStack() {
		assertNotNull(ProperArrayListStack.create());
	}

	@Test
	public void testEquals_emptyStacks1() {
		ProperStack<Object> stack1 = ProperArrayListStack.create();
		ProperStack<Object> stack2 = ProperArrayListStack.create();

		assertTrue(stack1.equals(stack2));
	}

	@Test
	public void testEquals_emptyStacks2() {
		ProperStack<Integer> stack1 = ProperArrayListStack.create();
		ProperStack<String> stack2 = ProperArrayListStack.create();

		assertTrue(stack1.equals(stack2));
	}

	@Test
	public void testEquals_oneEmptyStack() {
		ProperStack<Object> stack1 = ProperArrayListStack.create();
		ProperStack<Object> stack2 = ProperArrayListStack.create().push("a");

		assertFalse(stack1.equals(stack2));
	}

	@Test
	public void testEquals_oneElementSame() {
		ProperStack<Object> stack1 = ProperArrayListStack.create().push("a");
		ProperStack<Object> stack2 = ProperArrayListStack.create().push("a");

		assertTrue(stack1.equals(stack2));
	}

	@Test
	public void testEquals_oneElementDifferent() {
		ProperStack<Object> stack1 = ProperArrayListStack.create().push("a");
		ProperStack<Object> stack2 = ProperArrayListStack.create().push("b");

		assertFalse(stack1.equals(stack2));
	}

	@Test
	public void testIsEmpty_emptyStack() {
		ProperStack<String> stack = ProperArrayListStack.create();
		assertTrue(stack.isEmpty());
	}

	@Test
	public void testIsEmpty_nonEmptyStack() {
		ProperStack<String> stack = ProperArrayListStack.create();
		stack.push("a");
		assertFalse(stack.isEmpty());
	}

	@Test
	public void testIterator_emptyStack() {
		Iterator<String> iterator = ProperArrayListStack.<String>create().iterator();
		assertFalse(iterator.hasNext());

		try {
			iterator.next();
			fail("Should have thrown a NoSuchElementException");
		} catch (NoSuchElementException e) {
			// This is supposed to happen.
		}
	}

	@Test
	public void testIterator_multipleElements() {
		ProperStack<String> stack = ProperArrayListStack.<String>create()
				.push("a")
				.push("b")
				.push("c");
		Iterator<String> iterator = stack.iterator();

		assertTrue(iterator.hasNext());
		assertEquals("c", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("b", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("a", iterator.next());
		assertFalse(iterator.hasNext());

		try {
			iterator.next();
			fail("Should have thrown a NoSuchElementException");
		} catch (NoSuchElementException e) {
			// This is supposed to happen.
		}
	}

	@Test
	public void testIterator_oneElement() {
		ProperStack<String> stack = ProperArrayListStack.<String>create()
				.push("a");
		Iterator<String> iterator = stack.iterator();

		assertTrue(iterator.hasNext());
		assertEquals("a", iterator.next());
		assertFalse(iterator.hasNext());

		try {
			iterator.next();
			fail("Should have thrown a NoSuchElementException");
		} catch (NoSuchElementException e) {
			// This is supposed to happen.
		}
	}

	@Test
	public void testPeek_emptyStack() {
		try {
			ProperArrayListStack.create().peek();
			fail("Should have thrown an EmptyStackException");
		} catch (EmptyStackException e) {
			// This is supposed to happen.
		}
	}

	@Test
	public void testPop_emptyStack() {
		try {
			ProperArrayListStack.create().pop();
			fail("Should have thrown an EmptyStackException");
		} catch (EmptyStackException e) {
			// This is supposed to happen.
		}
	}

	@Test
	public void testPushAndPop_chained() {
		ProperStack<String> stack = ProperArrayListStack.<String>create()
				.push("a")
				.push("b")
				.push("c");
		assertEquals("c", stack.pop());
		assertEquals("b", stack.pop());
		assertEquals("a", stack.pop());
		assertTrue(stack.isEmpty());
	}

	@Test
	public void testPushPeekAndPop_oneElement() {
		ProperStack<String> stack = ProperArrayListStack.create();
		assertSame(stack, stack.push("a"));
		assertSame("a", stack.pop());
		assertTrue(stack.isEmpty());
	}

	@Test
	public void testPushPeekAndPop_threeElements() {
		ProperStack<String> stack = ProperArrayListStack.create();
		assertSame(stack, stack.push("a"));
		assertSame(stack, stack.push("b"));
		assertSame(stack, stack.push("c"));
		assertEquals("c", stack.peek());
		assertEquals("c", stack.pop());
		assertEquals("b", stack.peek());
		assertEquals("b", stack.pop());
		assertEquals("a", stack.peek());
		assertEquals("a", stack.pop());
		assertTrue(stack.isEmpty());
	}
}
