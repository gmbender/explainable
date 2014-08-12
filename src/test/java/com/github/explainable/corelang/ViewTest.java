package com.github.explainable.corelang;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static com.github.explainable.corelang.Atom.asMultisetAtom;
import static com.github.explainable.corelang.Atom.asSetAtom;
import static com.github.explainable.corelang.Atom.createSetAtom;
import static com.github.explainable.corelang.Terms.constant;
import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.multiset;
import static com.github.explainable.corelang.Terms.set;
import static com.github.explainable.util.MoreAsserts.assertEquivalent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link View}.
 */
public final class ViewTest {
	private static final Term DIST0 = dist();

	private static final Term DIST1 = dist();

	private static final Term MULTISET0 = multiset();

	private static final Term MULTISET1 = multiset();

	private static final Term SET0 = set();

	private static final Term SET1 = set();

	private static final Term CONST0 = constant(0L);

	private static final Term CONST1 = constant(1L);

	private static final ImmutableList<Relation> RELATIONS = ImmutableList.<Relation>of(
			RelationImpl.create("R0", ImmutableList.<String>of()),
			RelationImpl.create("R1", ImmutableList.of("col1")),
			RelationImpl.create("R2", ImmutableList.of("col1", "col2")));

	private View makeView(Term... arguments) {
		Atom atom = createSetAtom(
				RELATIONS.get(arguments.length),
				ImmutableList.copyOf(arguments));
		return View.create("Q", atom, ImmutableList.<Atom>of());
	}

	@Test
	public void testIsCompatibleWith_emptyArgumentList() {
		assertTrue(makeView().isCompatibleWith(makeView()));
	}

	@Test
	public void testIsCompatibleWith_singleArgDifferentTypes() {
		assertTrue(makeView(DIST0).isCompatibleWith(makeView(DIST0)));
		assertTrue(makeView(DIST0).isCompatibleWith(makeView(MULTISET0)));
		assertTrue(makeView(DIST0).isCompatibleWith(makeView(SET0)));
		assertTrue(makeView(DIST0).isCompatibleWith(makeView(CONST0)));

		assertTrue(makeView(MULTISET0).isCompatibleWith(makeView(DIST0)));
		assertTrue(makeView(MULTISET0).isCompatibleWith(makeView(MULTISET0)));
		assertTrue(makeView(MULTISET0).isCompatibleWith(makeView(SET0)));
		assertTrue(makeView(MULTISET0).isCompatibleWith(makeView(CONST0)));

		assertTrue(makeView(SET0).isCompatibleWith(makeView(DIST0)));
		assertTrue(makeView(SET0).isCompatibleWith(makeView(MULTISET0)));
		assertTrue(makeView(SET0).isCompatibleWith(makeView(SET0)));
		assertTrue(makeView(SET0).isCompatibleWith(makeView(CONST0)));

		assertTrue(makeView(CONST0).isCompatibleWith(makeView(DIST0)));
		assertTrue(makeView(CONST0).isCompatibleWith(makeView(MULTISET0)));
		assertTrue(makeView(CONST0).isCompatibleWith(makeView(SET0)));
		assertTrue(makeView(CONST0).isCompatibleWith(makeView(CONST0)));
	}

	@Test
	public void testIsCompatibleWith_singleArgSameType() {
		assertTrue(makeView(DIST0).isCompatibleWith(makeView(DIST1)));
		assertTrue(makeView(MULTISET0).isCompatibleWith(makeView(MULTISET1)));
		assertTrue(makeView(SET0).isCompatibleWith(makeView(SET1)));
		assertFalse(makeView(CONST0).isCompatibleWith(makeView(CONST1)));
	}

	@Test
	public void testIsCompatibleWith_twoArgsWithConstants1() {
		assertTrue(makeView(MULTISET0, MULTISET0).isCompatibleWith(makeView(CONST0, CONST0)));
		assertFalse(makeView(MULTISET0, MULTISET0).isCompatibleWith(makeView(CONST0, CONST1)));
		assertTrue(makeView(MULTISET0, MULTISET1).isCompatibleWith(makeView(CONST0, CONST0)));
		assertTrue(makeView(MULTISET0, MULTISET1).isCompatibleWith(makeView(CONST0, CONST1)));
	}

	@Test
	public void testIsCompatibleWith_twoArgsWithoutConstants() {
		// isCompatibleWith() cares about whether we're dealing with constants or variables,
		// but not whether we're dealing with multiset-existential, set-existential, or distinguished
		// variables. This is tested separately in UnionFindNode.create().
		assertTrue(makeView(MULTISET0, MULTISET0).isCompatibleWith(makeView(SET0, SET0)));
		assertTrue(makeView(MULTISET0, MULTISET0).isCompatibleWith(makeView(SET0, SET1)));
		assertTrue(makeView(MULTISET0, MULTISET1).isCompatibleWith(makeView(SET0, SET0)));
		assertTrue(makeView(MULTISET0, MULTISET1).isCompatibleWith(makeView(SET0, SET1)));
	}

	@Test
	public void testPrecedes_differentRelations() {
		RelationImpl rRel = RelationImpl.create("R", ImmutableList.<String>of());
		View view1 = View.create("Q", asSetAtom(rRel), ImmutableList.<Atom>of());

		RelationImpl sRel = RelationImpl.create("S", ImmutableList.<String>of());
		View view2 = View.create("Q", asSetAtom(sRel), ImmutableList.<Atom>of());

		assertFalse(view1.precedes(view2));
	}

	@Test
	public void testPrecedes_identical() {
		assertTrue(makeView().precedes(makeView()));
	}

	@Test
	public void testPrecedes_singleArgDifferentTypes() {
		assertTrue(makeView(DIST0).precedes(makeView(DIST0)));
		assertFalse(makeView(DIST0).precedes(makeView(MULTISET0)));
		assertFalse(makeView(DIST0).precedes(makeView(SET0)));
		assertFalse(makeView(DIST0).precedes(makeView(CONST0)));

		assertTrue(makeView(MULTISET0).precedes(makeView(DIST0)));
		assertTrue(makeView(MULTISET0).precedes(makeView(MULTISET0)));
		assertFalse(makeView(MULTISET0).precedes(makeView(SET0)));
		assertFalse(makeView(MULTISET0).precedes(makeView(CONST0)));

		assertTrue(makeView(SET0).precedes(makeView(DIST0)));
		assertTrue(makeView(SET0).precedes(makeView(MULTISET0)));
		assertTrue(makeView(SET0).precedes(makeView(SET0)));
		assertFalse(makeView(SET0).precedes(makeView(CONST0)));

		assertTrue(makeView(CONST0).precedes(makeView(DIST0)));
		assertFalse(makeView(CONST0).precedes(makeView(MULTISET0)));
		assertFalse(makeView(CONST0).precedes(makeView(SET0)));
		assertTrue(makeView(CONST0).precedes(makeView(CONST0)));
	}

	@Test
	public void testPrecedes_singleArgSameType() {
		assertTrue(makeView(DIST0).precedes(makeView(DIST1)));
		assertTrue(makeView(MULTISET0).precedes(makeView(MULTISET1)));
		assertTrue(makeView(SET0).precedes(makeView(SET1)));
		assertFalse(makeView(CONST0).precedes(makeView(CONST1)));
	}

	@Test
	public void testPrecedes_twoArgsSameType() {
		assertTrue(makeView(DIST0, DIST0).precedes(makeView(DIST0, DIST0)));
		assertTrue(makeView(DIST0, DIST0).precedes(makeView(DIST0, DIST1)));
		assertFalse(makeView(DIST0, DIST1).precedes(makeView(DIST0, DIST0)));
		assertTrue(makeView(DIST0, DIST1).precedes(makeView(DIST0, DIST1)));

		assertTrue(makeView(MULTISET0, MULTISET0).precedes(makeView(MULTISET0, MULTISET0)));
		assertFalse(makeView(MULTISET0, MULTISET0).precedes(makeView(MULTISET0, MULTISET1)));
		assertFalse(makeView(MULTISET0, MULTISET1).precedes(makeView(MULTISET0, MULTISET0)));
		assertTrue(makeView(MULTISET0, MULTISET1).precedes(makeView(MULTISET0, MULTISET1)));

		assertTrue(makeView(SET0, SET0).precedes(makeView(SET0, SET0)));
		assertFalse(makeView(SET0, SET0).precedes(makeView(SET0, SET1)));
		assertFalse(makeView(SET0, SET1).precedes(makeView(SET0, SET0)));
		assertTrue(makeView(SET0, SET1).precedes(makeView(SET0, SET1)));

		assertTrue(makeView(CONST0, CONST0).precedes(makeView(CONST0, CONST0)));
		assertFalse(makeView(CONST0, CONST0).precedes(makeView(CONST0, CONST1)));
		assertFalse(makeView(CONST0, CONST1).precedes(makeView(CONST0, CONST0)));
		assertTrue(makeView(CONST0, CONST1).precedes(makeView(CONST0, CONST1)));
	}

	@Test
	public void testPrecedes_twoMultisetExistentialArgs() {
		assertTrue(makeView(MULTISET0, MULTISET0).precedes(makeView(MULTISET0, MULTISET0)));
		assertFalse(makeView(MULTISET0, SET0).precedes(makeView(MULTISET0, MULTISET0)));
		assertFalse(makeView(SET0, MULTISET0).precedes(makeView(MULTISET0, MULTISET0)));
		assertTrue(makeView(SET0, SET0).precedes(makeView(MULTISET0, MULTISET0)));
	}

	/**
	 * This is a form of oracle testing. We've got two different implementations of
	 * isEquivalentTo(). One works is defined in terms of specialization maps, and the other is
	 * defined in terms of homomorphisms. We want to make sure they're returning the same answer.
	 */
	@Test
	public void testSingleAtomEquivalence() {
		Relation relation = RelationImpl.create("R", ImmutableList.of("X", "Y", "Z"));

		ImmutableList<? extends Term> terms = ImmutableList.of(
				dist(),
				dist(),
				dist(),
				multiset(),
				multiset(),
				multiset(),
				set(),
				set(),
				set(),
				constant("A"),
				constant("B"),
				constant("C"));

		List<Atom> atoms = Lists.newArrayList();
		for (Term term1 : terms) {
			for (Term term2 : terms) {
				for (Term term3 : terms) {
					atoms.add(asSetAtom(relation, term1, term2, term3));
				}
			}
		}

		for (Atom atom1 : atoms) {
			for (Atom atom2 : atoms) {
				boolean hasSpecializations
						= (new Specialization().extend(atom1, atom2) != null)
						&& (new Specialization().extend(atom2, atom1) != null);

				boolean hasHomomorphisms
						= (new Homomorphism().extend(atom1, atom2) != null)
						&& (new Homomorphism().extend(atom2, atom1) != null);

				assertEquals(
						"View: " + atom1 + " vs " + atom2,
						hasSpecializations,
						hasHomomorphisms);
			}
		}
	}

	@Test
	public void testPrecedes_setAndMultisetAtoms() {
		Relation rel = RelationImpl.create("Rel", ImmutableList.of("X", "Y"));
		View multisetView = View.create("Q",
				asMultisetAtom(rel, dist(), dist()),
				ImmutableList.<Atom>of());
		View setView = View.create("Q",
				asSetAtom(rel, dist(), dist()),
				ImmutableList.<Atom>of());

		assertTrue(multisetView.precedes(multisetView));
		assertFalse(multisetView.precedes(setView));
		assertTrue(setView.precedes(multisetView));
		assertTrue(setView.precedes(setView));
	}

	@Test
	public void testPrecedes_conditional_simple() {
		Relation user = RelationImpl.create("User", ImmutableList.of("uid", "name"));
		Relation friend = RelationImpl.create("Friend", ImmutableList.of("uid1", "uid2"));

		Term uid = dist();
		Term name = set();
		Term alice = constant("alice");

		// Q(uid) :- User(uid, name), {} when Friend('alice', uid)
		View uidsOfAliceFriends = View.create("Q",
				asSetAtom(user, uid, name),
				ImmutableList.of(asSetAtom(friend, alice, uid)));

		// Q(uid) :- User(uid, name), {}
		View uidsOfAllUsers = View.create("Q",
				asSetAtom(user, uid, name),
				ImmutableList.<Atom>of());

		assertTrue(uidsOfAliceFriends.precedes(uidsOfAliceFriends));
		assertTrue(uidsOfAliceFriends.precedes(uidsOfAllUsers));
		assertFalse(uidsOfAllUsers.precedes(uidsOfAliceFriends));
		assertTrue(uidsOfAllUsers.precedes(uidsOfAllUsers));
	}

	@Test
	public void testPrecedes_conditional_specializedSuccess() {
		Relation user = RelationImpl.create("User", ImmutableList.of("uid", "name"));
		Relation friend = RelationImpl.create("Friend", ImmutableList.of("uid1", "uid2"));

		Term uid = dist();
		Term one = constant(1L);
		Term two = constant(2L);

		// Q(name) :- User(2, name), {} when Friend(1, 2)
		View nameOfOneFriend = View.create("Q",
				asSetAtom(user, two, dist()),
				ImmutableList.of(asSetAtom(friend, one, two)));

		// Q(uid, name) :- User(uid, name), {} when Friend(1, uid)
		View namesOfAllFriends = View.create("Q",
				asSetAtom(user, uid, dist()),
				ImmutableList.of(asSetAtom(friend, one, uid)));

		assertTrue(nameOfOneFriend.precedes(nameOfOneFriend));
		assertTrue(nameOfOneFriend.precedes(namesOfAllFriends));
		assertFalse(namesOfAllFriends.precedes(nameOfOneFriend));
		assertTrue(namesOfAllFriends.precedes(namesOfAllFriends));
	}

	@Test
	public void testPrecedes_conditional_twoJoinsSuccess() {
		Relation user = RelationImpl.create("User", ImmutableList.of("uid", "name"));
		Relation friend = RelationImpl.create("Friend", ImmutableList.of("uid1", "uid2"));

		Term me = constant(1L);
		Term uid1 = dist();
		Term setUid2 = set();
		Term constantUid2 = constant(42L);

		View friendsOfOneFriend = View.create("Q",
				asSetAtom(user, uid1, dist()),
				ImmutableList.of(
						asSetAtom(friend, constantUid2, uid1),
						asSetAtom(friend, me, constantUid2)));

		View friendsOfAllFriends = View.create("Q",
				asSetAtom(user, uid1, dist()),
				ImmutableList.of(
						asSetAtom(friend, setUid2, uid1),
						asSetAtom(friend, me, setUid2)));

		assertTrue(friendsOfOneFriend.precedes(friendsOfOneFriend));
		assertTrue(friendsOfOneFriend.precedes(friendsOfAllFriends));
		assertFalse(friendsOfAllFriends.precedes(friendsOfOneFriend));
		assertTrue(friendsOfAllFriends.precedes(friendsOfAllFriends));
	}

	@Test
	public void testPrecedes_conditional_failInBody() {
		Relation user = RelationImpl.create("User", ImmutableList.of("uid", "name"));
		Relation friend = RelationImpl.create("Friend", ImmutableList.of("uid1", "uid2"));

		// Q(name) :- User(2, name), {} when Friend(1, uid2)
		View nameOfOneFriend = View.create("Q",
				asSetAtom(user, constant(2L), dist()),
				ImmutableList.of(asSetAtom(friend, constant(1L), set())));

		// Q(uid) :- User(uid, name), {} when Friend(1, uid)
		Term uid = dist();
		View uidsOfAllFriends = View.create("Q",
				asSetAtom(user, uid, set()),
				ImmutableList.of(asSetAtom(friend, constant(1L), uid)));

		assertTrue(nameOfOneFriend.precedes(nameOfOneFriend));
		assertFalse(nameOfOneFriend.precedes(uidsOfAllFriends));
		assertFalse(uidsOfAllFriends.precedes(nameOfOneFriend));
		assertTrue(uidsOfAllFriends.precedes(uidsOfAllFriends));
	}

	@Test
	public void testPrecedes_conditional_failInCondition() {
		Relation user = RelationImpl.create("User", ImmutableList.of("uid", "name"));
		Relation friend = RelationImpl.create("Friend", ImmutableList.of("uid1", "uid2"));

		// Q(name) :- User(uid, name), {} when Friend(1, 2)
		View nameOfOneFriend = View.create("Q",
				asSetAtom(user, set(), dist()),
				ImmutableList.of(asSetAtom(friend, constant(1L), constant(2L))));

		// Q(uid) :- User(uid, name), {} when Friend(1, uid)
		Term uid = dist();
		View namesOfAllFriends = View.create("Q",
				asSetAtom(user, uid, set()),
				ImmutableList.of(asSetAtom(friend, constant(1L), uid)));

		assertTrue(nameOfOneFriend.precedes(nameOfOneFriend));
		assertFalse(nameOfOneFriend.precedes(namesOfAllFriends));
		assertFalse(namesOfAllFriends.precedes(nameOfOneFriend));
		assertTrue(namesOfAllFriends.precedes(namesOfAllFriends));
	}

	@Test
	public void testPrecedes_conditional_folding() {
		Relation sailors = RelationImpl.create(
				"Sailors",
				ImmutableList.of("sid", "sname", "rating", "age"));

		Term rating = dist();
		View unconditional = View.create("Q",
				asMultisetAtom(sailors, multiset(), multiset(), rating, dist()),
				ImmutableList.<Atom>of());

		View conditional = View.create("Q",
				asMultisetAtom(sailors, multiset(), multiset(), rating, dist()),
				ImmutableList.of(asSetAtom(sailors, set(), set(), rating, set())));

		assertTrue(unconditional.precedes(conditional));
		assertTrue(conditional.precedes(unconditional));
	}

	@Test(expected = AssertionError.class)
	public void testIsValid_conditional_failWithMultisetVariable() {
		Relation rel = RelationImpl.create("Rel", ImmutableList.of("A"));
		Term multiset = multiset();

		View.create("Q",
				asSetAtom(rel, multiset),
				ImmutableList.of(asSetAtom(rel, multiset)));
	}

	@Test(expected = AssertionError.class)
	public void testIsValid_conditional_failWithSetVariable() {
		Relation rel = RelationImpl.create("Rel", ImmutableList.of("A"));
		Term set = set();

		View.create("Q",
				asSetAtom(rel, set),
				ImmutableList.of(asSetAtom(rel, set)));
	}

	@Test
	public void testCreate_noConditions() {
		Relation user = RelationImpl.create("User", ImmutableList.of("uid", "name"));

		Atom body = asMultisetAtom(user, dist(), dist());
		ImmutableList<Atom> oldConditions = ImmutableList.of();
		ImmutableList<Atom> newConditions = ImmutableList.of();

		assertEquivalent(View.create("Q", body, newConditions), View.convert(body, oldConditions));
	}

	@Test
	public void testCreate_oneCondition1() {
		Relation user = RelationImpl.create("User", ImmutableList.of("uid", "name"));
		Relation friend = RelationImpl.create("Friend", ImmutableList.of("uid1", "uid2"));

		Term uid = dist();
		Term name = set();
		Term alice = constant("alice");

		// Q(uid) :- User(uid, name), {} when Friend('alice', uid)
		Atom body = asMultisetAtom(user, uid, name);
		ImmutableList<Atom> oldConditions = ImmutableList.of(
				asMultisetAtom(friend, alice, uid));
		ImmutableList<Atom> newConditions = ImmutableList.of(
				asSetAtom(friend, alice, uid));

		assertEquivalent(View.create("Q", body, newConditions), View.convert(body, oldConditions));
	}

	@Test
	public void testCreate_oneCondition2() {
		Relation user = RelationImpl.create("User", ImmutableList.of("uid", "name"));
		Relation friend = RelationImpl.create("Friend", ImmutableList.of("uid1", "uid2"));

		Atom body = asMultisetAtom(user, constant(2L), dist());
		ImmutableList<Atom> oldConditions = ImmutableList.of(
				asMultisetAtom(friend, constant(1L), multiset()));
		ImmutableList<Atom> newConditions = ImmutableList.of(
				asSetAtom(friend, constant(1L), set()));

		assertEquivalent(View.create("Q", body, newConditions), View.convert(body, oldConditions));
	}

	@Test
	public void testCreate_twoConditions1() {
		Relation user = RelationImpl.create("User", ImmutableList.of("uid", "name"));
		Relation friend = RelationImpl.create("Friend", ImmutableList.of("uid1", "uid2"));

		Term me = constant(1L);
		Term distUid1 = dist();
		Term distUid2 = dist();
		Term setUid2 = set();

		Atom body = asMultisetAtom(user, distUid1, dist());
		ImmutableList<Atom> oldConditions = ImmutableList.of(
				asMultisetAtom(friend, distUid2, distUid1),
				asMultisetAtom(friend, me, distUid2));
		ImmutableList<Atom> newConditions = ImmutableList.of(
				asSetAtom(friend, setUid2, distUid1),
				asSetAtom(friend, me, setUid2));

		assertEquivalent(View.create("Q", body, newConditions), View.convert(body, oldConditions));
	}

	@Test
	public void testCreate_twoConditions2() {
		Relation user = RelationImpl.create("User", ImmutableList.of("uid", "name"));
		Relation friend = RelationImpl.create("Friend", ImmutableList.of("uid1", "uid2"));

		Term me = constant(1L);
		Term distUid1 = dist();
		Term multisetUid2 = multiset();
		Term setUid2 = set();

		Atom body = asMultisetAtom(user, distUid1, dist());
		ImmutableList<Atom> oldConditions = ImmutableList.of(
				asMultisetAtom(friend, multisetUid2, distUid1),
				asMultisetAtom(friend, me, multisetUid2));
		ImmutableList<Atom> newConditions = ImmutableList.of(
				asSetAtom(friend, setUid2, distUid1),
				asSetAtom(friend, me, setUid2));

		assertEquivalent(View.create("Q", body, newConditions), View.convert(body, oldConditions));
	}

	@Test
	public void testCreate_twoConditions3() {
		Relation user = RelationImpl.create("User", ImmutableList.of("uid", "name"));
		Relation friend = RelationImpl.create("Friend", ImmutableList.of("uid1", "uid2"));

		Term me = constant(1L);
		Term distUid1 = dist();
		Term setUid2 = set();

		Atom body = asMultisetAtom(user, distUid1, dist());
		ImmutableList<Atom> oldConditions = ImmutableList.of(
				asMultisetAtom(friend, setUid2, distUid1),
				asMultisetAtom(friend, me, setUid2));
		ImmutableList<Atom> newConditions = ImmutableList.of(
				asSetAtom(friend, setUid2, distUid1),
				asSetAtom(friend, me, setUid2));

		assertEquivalent(View.create("Q", body, newConditions), View.convert(body, oldConditions));
	}

	@Test
	public void testCreate_twoConditions4() {
		Relation user = RelationImpl.create("User", ImmutableList.of("uid", "name"));
		Relation friend = RelationImpl.create("Friend", ImmutableList.of("uid1", "uid2"));

		Term me = constant(1L);
		Term distUid1 = dist();
		Term constUid2 = constant(42L);

		Atom body = asMultisetAtom(user, distUid1, dist());
		ImmutableList<Atom> oldConditions = ImmutableList.of(
				asMultisetAtom(friend, constUid2, distUid1),
				asMultisetAtom(friend, me, constUid2));
		ImmutableList<Atom> newConditions = ImmutableList.of(
				asSetAtom(friend, constUid2, distUid1),
				asSetAtom(friend, me, constUid2));

		assertEquivalent(View.create("Q", body, newConditions), View.convert(body, oldConditions));
	}
}
