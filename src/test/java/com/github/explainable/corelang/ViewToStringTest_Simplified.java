package com.github.explainable.corelang;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static com.github.explainable.corelang.Terms.constant;
import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.multiset;
import static com.github.explainable.corelang.Terms.set;
import static com.github.explainable.corelang.ViewToStringMode.ADVANCED;
import static com.github.explainable.corelang.ViewToStringMode.SIMPLIFIED;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ViewToString} with setting {@code Mode.ADVANCED}.
 */
public class ViewToStringTest_Simplified {
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
				"Q(a, b, c) :- Rel(a, b, c)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testDistinctMultisetVariables() {
		Atom atom = Atom.asSetAtom(RELATION, multiset(), multiset(), multiset());

		assertEquals(
				"Q() :- Rel(a, b, c)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testDistinctSetVariables() {
		Atom atom = Atom.asSetAtom(RELATION, set(), set(), set());

		assertEquals(
				"Q() :- Rel(a, b, c)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testDistinctConstants() {
		Atom atom = Atom.asSetAtom(RELATION, constant(1L), constant(2L), constant(3L));

		assertEquals(
				"Q() :- Rel(1, 2, 3)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testAllEqualDistVariables() {
		Term term = dist();
		Atom atom = Atom.asSetAtom(RELATION, term, term, term);

		assertEquals(
				"Q(a) :- Rel(a, a, a)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testAllEqualMultisetVariables() {
		Term term = multiset();
		Atom atom = Atom.asSetAtom(RELATION, term, term, term);

		assertEquals(
				"Q() :- Rel(a, a, a)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testAllEqualSetVariables() {
		Term term = set();
		Atom atom = Atom.asSetAtom(RELATION, term, term, term);

		assertEquals(
				"Q() :- Rel(a, a, a)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testAllEqualConstants() {
		Term term = new Constant(1L);
		Atom atom = Atom.asSetAtom(RELATION, term, term, term);

		assertEquals(
				"Q() :- Rel(1, 1, 1)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualDistVariablesInFront() {
		Term term0 = dist();
		Term term1 = dist();
		Atom atom = Atom.asSetAtom(RELATION, term0, term0, term1);

		assertEquals(
				"Q(a, c) :- Rel(a, a, c)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualMultisetVariablesInFront() {
		Term term0 = multiset();
		Term term1 = multiset();
		Atom atom = Atom.asSetAtom(RELATION, term0, term0, term1);

		assertEquals(
				"Q() :- Rel(a, a, c)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualSetVariablesInFront() {
		Term term0 = multiset();
		Term term1 = set();
		Atom atom = Atom.asSetAtom(RELATION, term0, term0, term1);

		assertEquals(
				"Q() :- Rel(a, a, c)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualConstantsInFront() {
		Atom atom = Atom.asSetAtom(RELATION, constant(1L), constant(1L), constant(2L));

		assertEquals(
				"Q() :- Rel(1, 1, 2)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualDistVariablesInBack() {
		Term term0 = dist();
		Term term1 = dist();
		Atom atom = Atom.asSetAtom(RELATION, term0, term1, term1);
		assertEquals(
				"Q(a, b) :- Rel(a, b, b)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualMultisetVariablesInBack() {
		Term term0 = multiset();
		Term term1 = multiset();
		Atom atom = Atom.asSetAtom(RELATION, term0, term1, term1);
		assertEquals(
				"Q() :- Rel(a, b, b)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualSetVariablesInBack() {
		Term term0 = set();
		Term term1 = multiset();
		Atom atom = Atom.asSetAtom(RELATION, term0, term1, term1);
		assertEquals(
				"Q() :- Rel(a, b, b)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testTwoEqualConstantsInBack() {
		Atom atom = Atom.asSetAtom(RELATION, constant(1L), constant(2L), constant(2L));
		assertEquals(
				"Q() :- Rel(1, 2, 2)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testDistMultisetSet() {
		Atom atom = Atom.asSetAtom(RELATION, dist(), multiset(), set());
		assertEquals(
				"Q(a) :- Rel(a, b, c)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testSetMultisetDist() {
		Atom atom = Atom.asSetAtom(RELATION, set(), multiset(), dist());
		assertEquals(
				"Q(c) :- Rel(a, b, c)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testDistMultisetConstant() {
		Atom atom = Atom.asSetAtom(RELATION, dist(), multiset(), constant(1L));
		assertEquals(
				"Q(a) :- Rel(a, b, 1)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testConstantMultisetDist() {
		Atom atom = Atom.asSetAtom(RELATION, constant(1L), multiset(), dist());
		assertEquals(
				"Q(c) :- Rel(1, b, c)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testEqualDistConst() {
		Term term0 = dist();
		Term term1 = constant(1L);
		Atom atom = Atom.asSetAtom(RELATION, term0, term0, term1);
		assertEquals(
				"Q(a) :- Rel(a, a, 1)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testConstantEqualDist() {
		Term term0 = constant(1L);
		Term term1 = dist();
		Atom atom = Atom.asSetAtom(RELATION, term0, term1, term1);
		assertEquals(
				"Q(b) :- Rel(1, b, b)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testKnownBug1() {
		Relation relation2 = RelationImpl.create("Rel", ImmutableList.of("a", "b", "c", "d"));
		Term term0 = dist();
		Term term1 = constant(1L);
		Term term2 = multiset();
		Atom atom = Atom.asSetAtom(relation2, term0, term0, term1, term2);
		assertEquals(
				"Q(a) :- Rel(a, a, 1, d)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testKnownBug2() {
		Relation relation2 = RelationImpl.create("Rel", ImmutableList.of("a", "b", "c", "d"));
		Term term0 = dist();
		Term term1 = constant(1L);
		Term term2 = set();
		Atom atom = Atom.asSetAtom(relation2, term0, term0, term1, term2);
		assertEquals(
				"Q(a) :- Rel(a, a, 1, d)",
				new ViewToString("Q", SIMPLIFIED).appendBodyAtom(atom).toString());
	}

	@Test
	public void testMultiAtom_simple() {
		Term term0 = dist();
		Term term1 = multiset();
		Term term2 = set();
		Atom atom1 = Atom.asSetAtom(RELATION, term0, term1, term2);
		Atom atom2 = Atom.asSetAtom(RELATION, term2, term1, term0);
		assertEquals(
				"Q(a) :- Rel(a, b, c), Rel(c, b, a)",
				new ViewToString("Q", SIMPLIFIED)
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
				"Q() :- Rel(a, a, a), Rel(a2, a, a)",
				new ViewToString("Q", SIMPLIFIED)
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
				"Q() :- Gamma(c1, c1, c1), Gamma(c1_2, c1, c1)",
				new ViewToString("Q", SIMPLIFIED)
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
				"Q(a) :- Rel(a, a, c) \u22c9 Gamma(a, c2, c2)",
				new ViewToString("Q", SIMPLIFIED)
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
				"Q(a) :- Rel(a, a, c) \u22c9 Gamma(a, c2, c2), Psi(a, 42, x3)",
				new ViewToString("Q", SIMPLIFIED)
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
				"Q(a) :- Rel(a, a, c) \u22c9 Gamma(a, c2, c2)",
				new ViewToString("Q", SIMPLIFIED)
						.appendBodyAtom(atom1)
						.appendConditionAtom(atom2)
						.toString());
	}
}
