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

package com.github.explainable.corelang;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static com.github.explainable.corelang.Terms.constant;
import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.multiset;
import static com.github.explainable.corelang.Terms.set;
import static com.github.explainable.corelang.ViewToStringMode.ADVANCED;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ViewToString} with setting {@code Mode.ADVANCED}.
 */
public class ViewToStringTest_Advanced {
	private static final Relation RELATION = RelationImpl.create("Rel",
			ImmutableList.of("a", "b", "c"));

	@Test
	public void testName() {
		Atom atom = Atom.asSetAtom(RELATION, dist(), dist(), dist());

		assertEquals(
				"M_View(a, b, c) :- Rel(a, b, c; i) {}",
				new ViewToString("M_View", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testDistinctDistVariables() {
		Atom atom = Atom.asSetAtom(RELATION, dist(), dist(), dist());

		assertEquals(
				"Q(a, b, c) :- Rel(a, b, c; i) {}",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testDistinctMultisetVariables() {
		Atom atom = Atom.asSetAtom(RELATION, multiset(), multiset(), multiset());

		assertEquals(
				"Q() :- Rel(a, b, c; i) { a, b, c }",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testDistinctSetVariables() {
		Atom atom = Atom.asSetAtom(RELATION, set(), set(), set());

		assertEquals(
				"Q() :- Rel(a, b, c; i) {}",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testDistinctConstants() {
		Atom atom = Atom.asSetAtom(RELATION, constant(1L), constant(2L), constant(3L));

		assertEquals(
				"Q() :- Rel(1, 2, 3; i) {}",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testAllEqualDistVariables() {
		Term term = dist();
		Atom atom = Atom.asSetAtom(RELATION, term, term, term);

		assertEquals(
				"Q(a) :- Rel(a, a, a; i) {}",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testAllEqualMultisetVariables() {
		Term term = multiset();
		Atom atom = Atom.asSetAtom(RELATION, term, term, term);

		assertEquals(
				"Q() :- Rel(a, a, a; i) { a }",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testAllEqualSetVariables() {
		Term term = set();
		Atom atom = Atom.asSetAtom(RELATION, term, term, term);

		assertEquals(
				"Q() :- Rel(a, a, a; i) {}",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testAllEqualConstants() {
		Term term = new Constant(1L);
		Atom atom = Atom.asSetAtom(RELATION, term, term, term);

		assertEquals(
				"Q() :- Rel(1, 1, 1; i) {}",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualDistVariablesInFront() {
		Term term0 = dist();
		Term term1 = dist();
		Atom atom = Atom.asSetAtom(RELATION, term0, term0, term1);

		assertEquals(
				"Q(a, c) :- Rel(a, a, c; i) {}",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualMultisetVariablesInFront() {
		Term term0 = multiset();
		Term term1 = multiset();
		Atom atom = Atom.asSetAtom(RELATION, term0, term0, term1);

		assertEquals(
				"Q() :- Rel(a, a, c; i) { a, c }",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualSetVariablesInFront() {
		Term term0 = multiset();
		Term term1 = set();
		Atom atom = Atom.asSetAtom(RELATION, term0, term0, term1);

		assertEquals(
				"Q() :- Rel(a, a, c; i) { a }",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualConstantsInFront() {
		Atom atom = Atom.asSetAtom(RELATION, constant(1L), constant(1L), constant(2L));

		assertEquals(
				"Q() :- Rel(1, 1, 2; i) {}",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualDistVariablesInBack() {
		Term term0 = dist();
		Term term1 = dist();
		Atom atom = Atom.asSetAtom(RELATION, term0, term1, term1);
		assertEquals(
				"Q(a, b) :- Rel(a, b, b; i) {}",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualMultisetVariablesInBack() {
		Term term0 = multiset();
		Term term1 = multiset();
		Atom atom = Atom.asSetAtom(RELATION, term0, term1, term1);
		assertEquals(
				"Q() :- Rel(a, b, b; i) { a, b }",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualSetVariablesInBack() {
		Term term0 = set();
		Term term1 = multiset();
		Atom atom = Atom.asSetAtom(RELATION, term0, term1, term1);
		assertEquals(
				"Q() :- Rel(a, b, b; i) { b }",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualConstantsInBack() {
		Atom atom = Atom.asSetAtom(RELATION, constant(1L), constant(2L), constant(2L));
		assertEquals(
				"Q() :- Rel(1, 2, 2; i) {}",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testDistMultisetSet() {
		Atom atom = Atom.asSetAtom(RELATION, dist(), multiset(), set());
		assertEquals(
				"Q(a) :- Rel(a, b, c; i) { b }",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testSetMultisetDist() {
		Atom atom = Atom.asSetAtom(RELATION, set(), multiset(), dist());
		assertEquals(
				"Q(c) :- Rel(a, b, c; i) { b }",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testDistMultisetConstant() {
		Atom atom = Atom.asSetAtom(RELATION, dist(), multiset(), constant(1L));
		assertEquals(
				"Q(a) :- Rel(a, b, 1; i) { b }",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testConstantMultisetDist() {
		Atom atom = Atom.asSetAtom(RELATION, constant(1L), multiset(), dist());
		assertEquals(
				"Q(c) :- Rel(1, b, c; i) { b }",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testEqualDistConst() {
		Term term0 = dist();
		Term term1 = constant(1L);
		Atom atom = Atom.asSetAtom(RELATION, term0, term0, term1);
		assertEquals(
				"Q(a) :- Rel(a, a, 1; i) {}",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testConstantEqualDist() {
		Term term0 = constant(1L);
		Term term1 = dist();
		Atom atom = Atom.asSetAtom(RELATION, term0, term1, term1);
		assertEquals(
				"Q(b) :- Rel(1, b, b; i) {}",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testKnownBug1() {
		Relation relation2 = RelationImpl.create("Rel", ImmutableList.of("a", "b", "c", "d"));
		Term term0 = dist();
		Term term1 = constant(1L);
		Term term2 = multiset();
		Atom atom = Atom.asSetAtom(relation2, term0, term0, term1, term2);
		assertEquals(
				"Q(a) :- Rel(a, a, 1, d; i) { d }",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testKnownBug2() {
		Relation relation2 = RelationImpl.create("Rel", ImmutableList.of("a", "b", "c", "d"));
		Term term0 = dist();
		Term term1 = constant(1L);
		Term term2 = set();
		Atom atom = Atom.asSetAtom(relation2, term0, term0, term1, term2);
		assertEquals(
				"Q(a) :- Rel(a, a, 1, d; i) {}",
				new ViewToString("Q", ADVANCED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testMultiAtom_simple() {
		Term term0 = dist();
		Term term1 = multiset();
		Term term2 = set();
		Atom atom1 = Atom.asSetAtom(RELATION, term0, term1, term2);
		Atom atom2 = Atom.asSetAtom(RELATION, term2, term1, term0);
		assertEquals(
				"Q(a) :- Rel(a, b, c; i), Rel(c, b, a; i2) { b }",
				new ViewToString("Q", ADVANCED)
						.appendBodyAtom(atom1)
						.appendBodyAtom(atom2)
						.toString());
	}

	@Test
	public void testMultiAtom_duplicateColumnNames() {
		Term term0 = set();
		Term term1 = set();
		Atom atom1 = Atom.asSetAtom(RELATION, term0, term0, term0);
		Atom atom2 = Atom.asSetAtom(RELATION, term1, term0, term0);
		assertEquals(
				"Q() :- Rel(a, a, a; i), Rel(a2, a, a; i2) {}",
				new ViewToString("Q", ADVANCED)
						.appendBodyAtom(atom1)
						.appendBodyAtom(atom2)
						.toString());
	}

	@Test
	public void testMultiAtom_duplicateColumnNamesWithNumbers() {
		Relation relation = RelationImpl.create("Gamma", ImmutableList.of("c1", "c2", "c3"));
		Term term0 = set();
		Term term1 = set();
		Atom atom1 = Atom.asSetAtom(relation, term0, term0, term0);
		Atom atom2 = Atom.asSetAtom(relation, term1, term0, term0);
		assertEquals(
				"Q() :- Gamma(c1, c1, c1; i), Gamma(c1_2, c1, c1; i2) {}",
				new ViewToString("Q", ADVANCED)
						.appendBodyAtom(atom1)
						.appendBodyAtom(atom2)
						.toString());
	}

	@Test
	public void testMultiAtom_setWithConditionAtom() {
		Relation gamma = RelationImpl.create("Gamma", ImmutableList.of("c1", "c2", "c3"));
		Term term0 = dist();
		Term term1 = set();
		Term term2 = set();
		Atom atom1 = Atom.asSetAtom(RELATION, term0, term0, term1);
		Atom atom2 = Atom.asSetAtom(gamma, term0, term2, term2);
		assertEquals(
				"Q(a) :- Rel(a, a, c; i) \u22c9 Gamma(a, c2, c2; i2) {}",
				new ViewToString("Q", ADVANCED)
						.appendBodyAtom(atom1)
						.appendConditionAtom(atom2)
						.toString());
	}

	@Test
	public void testMultiAtom_setWithTwoConditionAtoms() {
		Relation gamma = RelationImpl.create("Gamma", ImmutableList.of("c1", "c2", "c3"));
		Relation psi = RelationImpl.create("Psi", ImmutableList.of("x1", "x2", "x3"));

		Term term0 = dist();
		Term term1 = set();
		Term term2 = set();
		Term term3 = constant(42);
		Term term4 = set();

		Atom atom1 = Atom.asSetAtom(RELATION, term0, term0, term1);
		Atom atom2 = Atom.asSetAtom(gamma, term0, term2, term2);
		Atom atom3 = Atom.asSetAtom(psi, term0, term3, term4);

		assertEquals(
				"Q(a) :- Rel(a, a, c; i) \u22c9 Gamma(a, c2, c2; i2), Psi(a, 42, x3; i3) {}",
				new ViewToString("Q", ADVANCED)
						.appendBodyAtom(atom1)
						.appendConditionAtom(atom2)
						.appendConditionAtom(atom3)
						.toString());
	}

	@Test
	public void testMultiAtom_multisetWithConditionAtom() {
		Relation gamma = RelationImpl.create("Gamma", ImmutableList.of("c1", "c2", "c3"));

		Term term0 = dist();
		Term term1 = multiset();
		Term term2 = set();

		Atom atom1 = Atom.asSetAtom(RELATION, term0, term0, term1);
		Atom atom2 = Atom.asSetAtom(gamma, term0, term2, term2);

		assertEquals(
				"Q(a) :- Rel(a, a, c; i) \u22c9 Gamma(a, c2, c2; i2) { c }",
				new ViewToString("Q", ADVANCED)
						.appendBodyAtom(atom1)
						.appendConditionAtom(atom2)
						.toString());
	}
}
