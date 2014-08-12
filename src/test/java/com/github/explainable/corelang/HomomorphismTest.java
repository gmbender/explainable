package com.github.explainable.corelang;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static com.github.explainable.corelang.Terms.constant;
import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.multiset;
import static com.github.explainable.corelang.Terms.set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 10/3/13 Time: 4:31 PM To change this template use
 * File | Settings | File Templates.
 */
public class HomomorphismTest {
	@Test
	public void testExtend_single() {
		Relation rel = RelationImpl.create("Rel", ImmutableList.of("X"));

		Atom dist = Atom.asSetAtom(rel, dist());
		Atom multiset = Atom.asSetAtom(rel, multiset());
		Atom set = Atom.asSetAtom(rel, set());
		Atom constant = Atom.asSetAtom(rel, constant("A"));

		assertNotNull(new Homomorphism().extend(dist, dist));
		assertNull(new Homomorphism().extend(dist, multiset));
		assertNull(new Homomorphism().extend(dist, set));
		assertNull(new Homomorphism().extend(dist, constant));

		assertNull(new Homomorphism().extend(multiset, dist));
		assertNotNull(new Homomorphism().extend(multiset, multiset));
		assertNull(new Homomorphism().extend(multiset, set));
		assertNull(new Homomorphism().extend(multiset, constant));

		assertNotNull(new Homomorphism().extend(set, dist));
		assertNotNull(new Homomorphism().extend(set, multiset));
		assertNotNull(new Homomorphism().extend(set, set));
		assertNotNull(new Homomorphism().extend(set, constant));

		assertNull(new Homomorphism().extend(constant, dist));
		assertNull(new Homomorphism().extend(constant, multiset));
		assertNull(new Homomorphism().extend(constant, set));
		assertNotNull(new Homomorphism().extend(constant, constant));
	}

	@Test
	public void testExtend_single_dist() {
		Relation rel = RelationImpl.create("Rel", ImmutableList.of("X"));

		Atom fromDist = Atom.asSetAtom(rel, dist());
		Atom toDist = Atom.asSetAtom(rel, dist());

		assertNotNull(new Homomorphism().extend(fromDist, toDist));
	}

	@Test
	public void testExtend_single_multiset() {
		Relation rel = RelationImpl.create("Rel", ImmutableList.of("X"));

		Atom fromMultiset = Atom.asSetAtom(rel, multiset());
		Atom toMultiset = Atom.asSetAtom(rel, multiset());

		assertNotNull(new Homomorphism().extend(fromMultiset, toMultiset));
	}

	@Test
	public void testExtend_single_set() {
		Relation rel = RelationImpl.create("Rel", ImmutableList.of("X"));

		Atom fromSet = Atom.asSetAtom(rel, set());
		Atom toSet = Atom.asSetAtom(rel, set());

		assertNotNull(new Homomorphism().extend(fromSet, toSet));
	}

	@Test
	public void testExtend_single_constant() {
		Relation rel = RelationImpl.create("Rel", ImmutableList.of("X"));

		Atom fromConstant = Atom.asSetAtom(rel, constant("1"));
		Atom toConstant = Atom.asSetAtom(rel, constant("2"));

		assertNull(new Homomorphism().extend(fromConstant, toConstant));
	}

	@Test
	public void testExtend_twice_same() {
		Relation rel = RelationImpl.create("Rel", ImmutableList.of("X"));
		ImmutableList<Term> terms = ImmutableList.of(
				dist(),
				dist(),
				multiset(),
				multiset(),
				set(),
				set(),
				constant("A"),
				constant("B"));

		for (Term fromTerm : terms) {
			for (Term toTerm : terms) {
				Atom from = Atom.asSetAtom(rel, fromTerm);
				Atom to = Atom.asSetAtom(rel, toTerm);
				Homomorphism hom = new Homomorphism();

				boolean attempt1 = hom.extend(from, to) != null;
				boolean attempt2 = hom.extend(from, to) != null;
				assertEquals(attempt1, attempt2);
			}
		}
	}

	@Test
	public void testExtend_equate_dist() {
		Relation rel = RelationImpl.create("Rel", ImmutableList.of("X", "Y"));

		Atom from1 = Atom.asSetAtom(rel, dist(), dist());
		Atom from2 = Atom.asSetAtom(rel, dist(), set());

		Term toTerm = dist();
		Atom to = Atom.asSetAtom(rel, toTerm, toTerm);

		assertNull(new Homomorphism().extend(from1, to));
		assertNotNull(new Homomorphism().extend(from2, to));
	}

	@Test
	public void testExtend_equate_multiset() {
		Relation rel = RelationImpl.create("Rel", ImmutableList.of("X", "Y"));

		Atom atom1 = Atom.asSetAtom(rel, set(), set());
		Atom atom2 = Atom.asSetAtom(rel, multiset(), multiset());

		assertNotNull(new Homomorphism().extend(atom1, atom2));
		assertNull(new Homomorphism().extend(atom2, atom1));
	}

	@Test
	public void testExtend_equate_set() {
		Relation rel = RelationImpl.create("Rel", ImmutableList.of("X", "Y"));

		Atom from = Atom.asSetAtom(rel, set(), set());

		Term toTerm = set();
		Atom to = Atom.asSetAtom(rel, toTerm, toTerm);

		assertNotNull(new Homomorphism().extend(from, to));
	}

	@Test
	public void testExtend_equate_constant() {
		Relation rel = RelationImpl.create("Rel", ImmutableList.of("X", "Y"));

		ImmutableList<Term> fromTerms = ImmutableList.of(
				set(),
				set(),
				constant("A"),
				constant("B"));

		Term toTerm = constant("A");
		Atom to = Atom.asSetAtom(rel, toTerm, toTerm);

		for (Term fromTerm1 : fromTerms) {
			for (Term fromTerm2 : fromTerms) {
				Atom from = Atom.asSetAtom(rel, fromTerm1, fromTerm2);
				assertEquals(
						!fromTerm1.equals(constant("B")) && !fromTerm2.equals(constant("B")),
						new Homomorphism().extend(from, to) != null);
			}
		}
	}

	@Test
	public void testExtend_different_relations() {
		Relation alpha = RelationImpl.create("Alpha", ImmutableList.of("A"));
		Relation beta = RelationImpl.create("Beta", ImmutableList.of("B"));

		Atom from = Atom.asSetAtom(alpha, dist());
		Atom to = Atom.asSetAtom(beta, dist());

		assertNull(new Homomorphism().extend(from, to));
	}

	@Test
	public void testExtend_setAndMultisetAtoms() {
		Relation rel = RelationImpl.create("Rel", ImmutableList.of("X", "Y"));

		Atom setAtom = Atom.asSetAtom(rel, set(), set());
		Atom multisetAtom = Atom.asMultisetAtom(rel, set(), set());

		assertNotNull(new Homomorphism().extend(setAtom, setAtom));
		assertNotNull(new Homomorphism().extend(setAtom, multisetAtom));
		assertNull(new Homomorphism().extend(multisetAtom, setAtom));
		assertNotNull(new Homomorphism().extend(multisetAtom, multisetAtom));
	}
}
