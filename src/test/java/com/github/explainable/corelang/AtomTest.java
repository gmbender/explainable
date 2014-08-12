package com.github.explainable.corelang;

import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.github.explainable.corelang.Terms.constant;
import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.multiset;
import static com.github.explainable.corelang.Terms.set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for {@link Atom}.
 */
public class AtomTest {
	private Relation rel1;

	private Relation rel2;

	@Before
	public void setUp() {
		rel1 = RelationImpl.create("Rel1", ImmutableList.of("x"));
		rel2 = RelationImpl.create("Rel2", ImmutableList.of("x", "y", "z"));
	}

	@After
	public void tearDown() {
		rel1 = null;
		rel2 = null;
	}

	@Test
	public void testUnifyWith_differentRelation() {
		Atom atom1 = Atom.asMultisetAtom(rel1, dist());
		Atom atom2 = Atom.asMultisetAtom(rel2, dist(), dist(), dist());

		assertNull(atom1.unifyWith(atom2));
	}

	@Test
	public void testUnifyWith_sameRelation() {
		Atom atom1 = Atom.asMultisetAtom(rel1, dist());
		Atom atom2 = Atom.asMultisetAtom(rel1, dist());

		assertEquals(rel1, atom1.unifyWith(atom2).relation());
	}

	@Test
	public void testUnifyWith_singleArg() {
		Atom dist = Atom.asMultisetAtom(rel1, dist());
		Atom multiset = Atom.asMultisetAtom(rel1, multiset());
		Atom set = Atom.asMultisetAtom(rel1, set());
		Atom strConst = Atom.asMultisetAtom(rel1, constant("hello"));

		assertEquals(
				TermType.DIST_VARIABLE,
				dist.unifyWith(dist).arguments().get(0).type());

		assertEquals(
				TermType.DIST_VARIABLE,
				dist.unifyWith(multiset).arguments().get(0).type());

		assertEquals(
				TermType.DIST_VARIABLE,
				dist.unifyWith(set).arguments().get(0).type());

		assertEquals(
				constant("hello"),
				dist.unifyWith(strConst).arguments().get(0));

		assertEquals(
				TermType.DIST_VARIABLE,
				multiset.unifyWith(dist).arguments().get(0).type());

		assertEquals(
				TermType.MULTISET_VARIABLE,
				multiset.unifyWith(multiset).arguments().get(0).type());

		assertEquals(
				TermType.MULTISET_VARIABLE,
				multiset.unifyWith(set).arguments().get(0).type());

		assertEquals(
				constant("hello"),
				multiset.unifyWith(strConst).arguments().get(0));

		assertEquals(
				TermType.DIST_VARIABLE,
				set.unifyWith(dist).arguments().get(0).type());

		assertEquals(
				TermType.MULTISET_VARIABLE,
				set.unifyWith(multiset).arguments().get(0).type());

		assertEquals(
				TermType.SET_VARIABLE,
				set.unifyWith(set).arguments().get(0).type());

		assertEquals(
				constant("hello"),
				set.unifyWith(strConst).arguments().get(0));

		assertEquals(
				constant("hello"),
				strConst.unifyWith(dist).arguments().get(0));

		assertEquals(
				constant("hello"),
				strConst.unifyWith(multiset).arguments().get(0));

		assertEquals(
				constant("hello"),
				strConst.unifyWith(set).arguments().get(0));

		assertEquals(
				constant("hello"),
				strConst.unifyWith(strConst).arguments().get(0));
	}

	@Test
	public void testUnifyWith_copyVariable() {
		Atom multisetCopy = Atom.asMultisetAtom(rel1, dist());
		Atom setCopy = Atom.asSetAtom(rel1, dist());

		assertEquals(
				TermType.MULTISET_VARIABLE,
				multisetCopy.unifyWith(multisetCopy).getCopyVariable().type());

		assertEquals(
				TermType.MULTISET_VARIABLE,
				multisetCopy.unifyWith(setCopy).getCopyVariable().type());

		assertEquals(
				TermType.MULTISET_VARIABLE,
				setCopy.unifyWith(multisetCopy).getCopyVariable().type());

		assertEquals(
				TermType.SET_VARIABLE,
				setCopy.unifyWith(setCopy).getCopyVariable().type());
	}

	@Test
	public void testUnifyWith_mismatchedConstants() {
		Atom helloAtom = Atom.asMultisetAtom(rel1, constant("hello"));
		Atom worldAtom = Atom.asMultisetAtom(rel1, constant("world"));

		assertNull(helloAtom.unifyWith(worldAtom));
	}

	@Test
	public void testUnifyWith_multiAtomSuccess1() {
		Atom atom1 = Atom.asMultisetAtom(rel2, constant("duck"), multiset(), constant("goose"));

		Term commonTerm = multiset();
		Atom atom2 = Atom.asMultisetAtom(rel2, commonTerm, commonTerm, multiset());

		Atom unifier = atom1.unifyWith(atom2);

		assertEquals(
				ImmutableList.of(constant("duck"), constant("duck"), constant("goose")),
				unifier.arguments());
	}

	@Test
	public void testUnifyWith_multiAtomSuccess2() {
		Term commonTerm1 = multiset();
		Atom atom1 = Atom.asMultisetAtom(rel2, commonTerm1, commonTerm1, dist());

		Term commonTerm2 = multiset();
		Atom atom2 = Atom.asMultisetAtom(rel2, dist(), commonTerm2, commonTerm2);

		List<Term> unifierArgs = atom1.unifyWith(atom2).arguments();

		assertEquals(unifierArgs.get(0), unifierArgs.get(1));
		assertEquals(unifierArgs.get(0), unifierArgs.get(2));
		assertEquals(TermType.DIST_VARIABLE, unifierArgs.get(0).type());
	}

	@Test
	public void testUnifyWith_multiAtomFailure() {
		Term commonTerm = multiset();
		Atom atom1 = Atom.asMultisetAtom(rel2, commonTerm, commonTerm, commonTerm);
		Atom atom2 = Atom.asMultisetAtom(rel2, constant("x"), constant("x"), constant("y"));

		assertNull(atom1.unifyWith(atom2));
	}
}
