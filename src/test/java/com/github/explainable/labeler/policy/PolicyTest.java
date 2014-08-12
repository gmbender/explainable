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

package com.github.explainable.labeler.policy;

import com.github.explainable.corelang.Atom;
import com.github.explainable.corelang.Relation;
import com.github.explainable.corelang.RelationImpl;
import com.github.explainable.corelang.Terms;
import com.github.explainable.corelang.View;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link Policy}.
 */
public class PolicyTest {
	private View viewX;

	private View viewY;

	@Before
	public void setUp() {
		Relation relation = RelationImpl.create("R", ImmutableList.of("x", "y"));

		viewX = View.asView(Atom.asMultisetAtom(relation, Terms.dist(), Terms.multiset()));
		viewY = View.asView(Atom.asMultisetAtom(relation, Terms.multiset(), Terms.dist()));
	}

	@After
	public void tearDown() {
		viewX = null;
		viewY = null;
	}

	@Test
	public void testEvaluate() throws Exception {
		assertFalse(Policy.FALSE.evaluate(ImmutableSet.<View>of()));
		assertTrue(Policy.TRUE.evaluate(ImmutableSet.<View>of()));
		assertFalse(Policy.of(viewX).evaluate(ImmutableSet.<View>of(viewY)));
		assertTrue(Policy.of(viewX).evaluate(ImmutableSet.<View>of(viewX, viewY)));

		assertFalse(Policy.FALSE.or(Policy.FALSE).evaluate(ImmutableSet.<View>of()));
		assertTrue(Policy.FALSE.or(Policy.TRUE).evaluate(ImmutableSet.<View>of()));
		assertTrue(Policy.TRUE.or(Policy.FALSE).evaluate(ImmutableSet.<View>of()));
		assertTrue(Policy.TRUE.or(Policy.TRUE).evaluate(ImmutableSet.<View>of()));

		assertFalse(Policy.FALSE.and(Policy.FALSE).evaluate(ImmutableSet.<View>of()));
		assertFalse(Policy.FALSE.and(Policy.TRUE).evaluate(ImmutableSet.<View>of()));
		assertFalse(Policy.TRUE.and(Policy.FALSE).evaluate(ImmutableSet.<View>of()));
		assertTrue(Policy.TRUE.and(Policy.TRUE).evaluate(ImmutableSet.<View>of()));
	}

	@Test
	public void testWhySo() throws Exception {
		assertEquals(Policy.FALSE, Policy.FALSE.whySo(ImmutableSet.<View>of()));
		assertEquals(Policy.TRUE, Policy.TRUE.whySo(ImmutableSet.<View>of()));
		assertEquals(Policy.FALSE, Policy.of(viewX).whySo(ImmutableSet.<View>of(viewY)));
		assertEquals(Policy.of(viewX), Policy.of(viewX).whySo(ImmutableSet.<View>of(viewX, viewY)));

		Policy add0To0 = Policy.FALSE.or(Policy.FALSE);
		Policy addXToX = Policy.of(viewX).or(Policy.of(viewX));

		assertEquals(add0To0, addXToX.whySo(ImmutableSet.<View>of(viewY)));
		assertEquals(addXToX, addXToX.whySo(ImmutableSet.<View>of(viewX)));

		Policy mult0By0 = Policy.FALSE.and(Policy.FALSE);
		Policy multXByX = Policy.of(viewX).and(Policy.of(viewX));

		assertEquals(mult0By0, multXByX.whySo(ImmutableSet.<View>of(viewY)));
		assertEquals(multXByX, multXByX.whySo(ImmutableSet.<View>of(viewX)));
	}

	@Test
	public void testWhyNot() throws Exception {
		assertEquals(Policy.FALSE, Policy.FALSE.whyNot(ImmutableSet.<View>of()));
		assertEquals(Policy.TRUE, Policy.TRUE.whyNot(ImmutableSet.<View>of()));
		assertEquals(Policy.of(viewX), Policy.of(viewX).whyNot(ImmutableSet.<View>of(viewY)));
		assertEquals(Policy.TRUE, Policy.of(viewX).whyNot(ImmutableSet.<View>of(viewX, viewY)));

		Policy addXToX = Policy.of(viewX).or(Policy.of(viewX));
		Policy add1To1 = Policy.TRUE.or(Policy.TRUE);

		assertEquals(addXToX, addXToX.whyNot(ImmutableSet.<View>of(viewY)));
		assertEquals(add1To1, addXToX.whyNot(ImmutableSet.<View>of(viewX)));

		Policy multXByX = Policy.of(viewX).and(Policy.of(viewX));
		Policy mult1By1 = Policy.TRUE.and(Policy.TRUE);

		assertEquals(multXByX, multXByX.whyNot(ImmutableSet.<View>of(viewY)));
		assertEquals(mult1By1, multXByX.whyNot(ImmutableSet.<View>of(viewX)));
	}

	@Test
	public void testSimplify_basic() throws Exception {
		assertEquals(Policy.FALSE, Policy.FALSE.simplify());
		assertEquals(Policy.TRUE, Policy.TRUE.simplify());
		assertEquals(Policy.of(viewX), Policy.of(viewX).simplify());
		assertEquals(Policy.of(viewY), Policy.of(viewY).simplify());
	}

	@Test
	public void testSimplify_add() throws Exception {
		assertEquals(
				Policy.of(viewX),
				Policy.FALSE.or(Policy.of(viewX)).simplify());

		assertEquals(
				Policy.of(viewX),
				Policy.of(viewX).or(Policy.FALSE).simplify());

		assertEquals(
				Policy.TRUE,
				Policy.TRUE.or(Policy.of(viewX)).simplify());

		assertEquals(
				Policy.TRUE,
				Policy.of(viewX).or(Policy.TRUE).simplify());

		assertEquals(
				Policy.TRUE,
				Policy.of(viewX).or(Policy.FALSE.or(Policy.TRUE)).simplify());

		assertEquals(
				Policy.TRUE,
				Policy.TRUE.or(Policy.FALSE).or(Policy.of(viewX)).simplify());
	}

	@Test
	public void testSimplify_product() throws Exception {
		assertEquals(
				Policy.FALSE,
				Policy.FALSE.and(Policy.of(viewX)).simplify());

		assertEquals(
				Policy.FALSE,
				Policy.of(viewX).and(Policy.FALSE).simplify());

		assertEquals(
				Policy.of(viewX),
				Policy.TRUE.and(Policy.of(viewX)).simplify());

		assertEquals(
				Policy.of(viewX),
				Policy.of(viewX).and(Policy.TRUE).simplify());

		assertEquals(
				Policy.FALSE,
				Policy.of(viewX).and(Policy.FALSE.and(Policy.TRUE)).simplify());

		assertEquals(
				Policy.FALSE,
				Policy.TRUE.and(Policy.FALSE).and(Policy.of(viewX)).simplify());
	}
}
