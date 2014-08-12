package com.github.explainable.benchmark;

import com.github.explainable.corelang.Atom;
import com.github.explainable.corelang.Conjunction;
import com.github.explainable.corelang.Relation;
import com.github.explainable.corelang.RelationImpl;
import com.github.explainable.corelang.Term;
import com.github.explainable.corelang.View;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static com.github.explainable.corelang.Terms.constant;
import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.set;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit testNextWithDefaultDist_s for {@link ConjunctionGenerator}.
 */
public class ConjunctionGeneratorTest {
	private Random random = null;

	@Before
	public void setUp() {
		random = new Random(-1702765630L);
	}

	@After
	public void tearDown() {
		random = null;
	}

	@Test
	public void testNextWithDefaultDist_simple() {
		Relation relR = RelationImpl.create("R", ImmutableList.of("x", "y"));
		Relation relS = RelationImpl.create("S", ImmutableList.of("z", "w"));

		Term dist1 = dist();
		Term dist2 = dist();
		View view1 = View.asView(
				Atom.asMultisetAtom(relR, dist1, dist2),
				Atom.asSetAtom(relS, dist1, dist2));

		ConjunctionGenerator viewGen = ConjunctionGenerator.create(ImmutableList.of(view1), random);

		Conjunction actual = viewGen.nextWithDefaultDist();

		Conjunction expected = Conjunction.create(ImmutableList.of(
				Atom.asMultisetAtom(relR, dist1, dist2),
				Atom.asSetAtom(relS, dist1, dist2)));

		assertTrue(expected.isHomomorphicTo(actual));
	}

	@Test
	public void testNextWithDefaultDist_recursive() {
		Relation relR = RelationImpl.create("R", ImmutableList.of("x"));

		View view = View.asView(
				Atom.asMultisetAtom(relR, dist()),
				Atom.asSetAtom(relR, set()));

		Conjunction expected = Conjunction.create(ImmutableList.of(
				Atom.asMultisetAtom(relR, dist()),
				Atom.asSetAtom(relR, set())));

		ConjunctionGenerator viewGen = ConjunctionGenerator.create(ImmutableList.of(view), random);
		Conjunction actual = viewGen.nextWithDefaultDist();

		System.out.println(actual);
		System.out.println(expected);
		assertTrue(expected.isHomomorphicTo(actual));
	}

	@Test
	public void testNextWithDefaultDist_chainSubstitution1() {
		Relation relR = RelationImpl.create("R", ImmutableList.of("x", "y"));
		Relation relS = RelationImpl.create("S", ImmutableList.of("z", "w"));
		Relation relT = RelationImpl.create("T", ImmutableList.of("a", "b"));

		Term dist1 = dist();
		Term dist2 = dist();

		View view1 = View.asView(
				Atom.asMultisetAtom(relR, dist1, dist2),
				Atom.asSetAtom(relS, dist1, dist2));

		View view2 = View.asView(
				Atom.asMultisetAtom(relS, constant(1L), constant(2L)),
				Atom.asSetAtom(relT, set(), set()));

		Conjunction expectedOption1 = Conjunction.create(ImmutableList.of(
				Atom.asMultisetAtom(relS, constant(1L), constant(2L)),
				Atom.asSetAtom(relT, set(), set())));

		Conjunction expectedOption2 = Conjunction.create(ImmutableList.of(
				Atom.asMultisetAtom(relR, constant(1L), constant(2L)),
				Atom.asSetAtom(relS, constant(1L), constant(2L)),
				Atom.asSetAtom(relT, set(), set())));

		ConjunctionGenerator viewGen = ConjunctionGenerator.create(
				ImmutableList.of(view1, view2),
				random);

		boolean[] witnessed = new boolean[2];

		for (int i = 0; i < 10; i++) {
			Conjunction actual = viewGen.nextWithDefaultDist();

			if (expectedOption1.isHomomorphicTo(actual)) {
				witnessed[0] = true;
			} else if (expectedOption2.isHomomorphicTo(actual)) {
				witnessed[1] = true;
			} else {
				fail();
			}
		}

		// There are two possible outputs. Make sure that each of them is observed at least once
		// among our ten runs.
		assertTrue(witnessed[0]);
		assertTrue(witnessed[1]);
	}

	@Test
	public void testNextWithDefaultDist_chainSubstitution2() {
		Relation relR = RelationImpl.create("R", ImmutableList.of("x", "y"));
		Relation relS = RelationImpl.create("S", ImmutableList.of("z", "w"));
		Relation relT = RelationImpl.create("T", ImmutableList.of("a", "b"));

		View view1 = View.asView(
				Atom.asMultisetAtom(relR, set(), set()),
				Atom.asSetAtom(relS, constant(1L), constant(2L)));

		Term dist1 = dist();
		Term dist2 = dist();

		View view2 = View.asView(
				Atom.asMultisetAtom(relS, dist1, dist2),
				Atom.asSetAtom(relT, dist1, dist2));

		Conjunction expectedOption1 = Conjunction.create(ImmutableList.of(
				Atom.asMultisetAtom(relS, dist1, dist2),
				Atom.asSetAtom(relT, dist1, dist2)));

		Conjunction expectedOption2 = Conjunction.create(ImmutableList.of(
				Atom.asMultisetAtom(relR, set(), set()),
				Atom.asSetAtom(relS, constant(1L), constant(2L)),
				Atom.asSetAtom(relT, constant(1L), constant(2L))));

		ConjunctionGenerator viewGen = ConjunctionGenerator.create(
				ImmutableList.of(view1, view2),
				random);

		boolean[] witnessed = new boolean[2];

		for (int i = 0; i < 10; i++) {
			Conjunction actual = viewGen.nextWithDefaultDist();

			if (expectedOption1.isHomomorphicTo(actual)) {
				witnessed[0] = true;
			} else if (expectedOption2.isHomomorphicTo(actual)) {
				witnessed[1] = true;
			} else {
				fail();
			}
		}

		// There are two possible outputs. Make sure that each of them is observed at least once
		// among our ten runs.
		assertTrue(witnessed[0]);
		assertTrue(witnessed[1]);
	}

	@Test
	public void testNextWithNoDist_simple() {
		Relation relR = RelationImpl.create("R", ImmutableList.of("x", "y"));
		Relation relS = RelationImpl.create("S", ImmutableList.of("z", "w"));

		Term dist1 = dist();
		Term dist2 = dist();
		View view1 = View.asView(
				Atom.asMultisetAtom(relR, dist1, dist2),
				Atom.asSetAtom(relS, dist1, dist2));

		ConjunctionGenerator viewGen = ConjunctionGenerator.create(ImmutableList.of(view1), random);

		Conjunction actual = viewGen.nextWithNoDist();

		Term set1 = set();
		Term set2 = set();
		Conjunction expected = Conjunction.create(ImmutableList.of(
				Atom.asMultisetAtom(relR, set1, set2),
				Atom.asSetAtom(relS, set1, set2)));

		assertTrue(expected.isHomomorphicTo(actual));
	}

	@Test
	public void testNextWithRandomDist_simple() {
		Relation relR = RelationImpl.create("R", ImmutableList.of("x", "y"));
		Relation relS = RelationImpl.create("S", ImmutableList.of("z", "w"));

		Term viewDist1 = dist();
		Term viewDist2 = dist();
		View view1 = View.asView(
				Atom.asMultisetAtom(relR, viewDist1, viewDist2),
				Atom.asSetAtom(relS, viewDist1, viewDist2));

		ConjunctionGenerator viewGen = ConjunctionGenerator.create(ImmutableList.of(view1), random);

		Term set1 = set();
		Term set2 = set();

		Term dist1 = dist();
		Term dist2 = dist();

		Conjunction[] expectedOptions = new Conjunction[] {
				Conjunction.create(ImmutableList.of(
						Atom.asMultisetAtom(relR, set1, set2),
						Atom.asSetAtom(relS, set1, set2))),
				Conjunction.create(ImmutableList.of(
						Atom.asMultisetAtom(relR, set1, dist2),
						Atom.asSetAtom(relS, set1, dist2))),
				Conjunction.create(ImmutableList.of(
						Atom.asMultisetAtom(relR, dist1, set2),
						Atom.asSetAtom(relS, dist1, set2))),
				Conjunction.create(ImmutableList.of(
						Atom.asMultisetAtom(relR, dist1, dist2),
						Atom.asSetAtom(relS, dist1, dist2)))
		};

		boolean[] observed = new boolean[4];

		// There are two possible outputs. Make sure that each of them is observed at least once
		// among our 100 trials.
		for (int trial = 0; trial < 100; trial++) {
			Conjunction actual = viewGen.nextWithRandomDist(1.0);

			if (expectedOptions[0].isHomomorphicTo(actual)) {
				observed[0] = true;
			} else if (expectedOptions[1].isHomomorphicTo(actual)) {
				observed[1] = true;
			} else if (expectedOptions[2].isHomomorphicTo(actual)) {
				observed[2] = true;
			} else if (expectedOptions[3].isHomomorphicTo(actual)) {
				observed[3] = true;
			} else {
				fail("Illegal output: " + actual);
			}
		}

		assertTrue(observed[0]);
		assertTrue(observed[1]);
		assertTrue(observed[2]);
		assertTrue(observed[3]);
	}
}