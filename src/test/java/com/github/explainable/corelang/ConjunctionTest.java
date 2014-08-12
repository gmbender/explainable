package com.github.explainable.corelang;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.set;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for {@link Conjunction}.
 */
public class ConjunctionTest {
	@Test
	public void testFindHomomorphism_dist_dist() {
		Relation relation = RelationImpl.create("R", ImmutableList.of("X"));

		Atom atom1 = Atom.asSetAtom(relation, dist());
		Atom atom2 = Atom.asSetAtom(relation, dist());

		Conjunction singleAtom = Conjunction.create(ImmutableList.of(atom1));
		Conjunction twoAtom = Conjunction.create(ImmutableList.of(atom1, atom2));

		assertNotNull(singleAtom.findHomomorphism(twoAtom));
		assertNull(twoAtom.findHomomorphism(singleAtom));
	}

	@Test
	public void testFindHomomorphism_dist_set() {
		Relation relation = RelationImpl.create("R", ImmutableList.of("X"));

		Atom atom1 = Atom.asSetAtom(relation, dist());
		Atom atom2 = Atom.asSetAtom(relation, set());

		Conjunction singleAtom = Conjunction.create(ImmutableList.of(atom1));
		Conjunction twoAtom = Conjunction.create(ImmutableList.of(atom1, atom2));

		assertNotNull(singleAtom.findHomomorphism(twoAtom));
		assertNotNull(twoAtom.findHomomorphism(singleAtom));
	}

	@Test
	public void testFindHomomorphism_set_set() {
		Relation relation = RelationImpl.create("R", ImmutableList.of("X"));

		Atom atom1 = Atom.asSetAtom(relation, set());
		Atom atom2 = Atom.asSetAtom(relation, set());

		Conjunction singleAtom = Conjunction.create(ImmutableList.of(atom1));
		Conjunction twoAtom = Conjunction.create(ImmutableList.of(atom1, atom2));

		assertNotNull(singleAtom.findHomomorphism(twoAtom));
		assertNotNull(twoAtom.findHomomorphism(singleAtom));
	}
}
