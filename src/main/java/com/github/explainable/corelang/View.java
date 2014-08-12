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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface representing an annotated view containing a single body atom and arbitrarily many
 * (i.e., zero, one, or multiple) conditions atoms.
 */
public final class View {
	private final String name;

	private final Atom body;

	private final ImmutableList<Atom> conditions;

	private final ImmutableSet<Relation> relations;

	private View(String name, Atom body, List<Atom> conditions, Set<Relation> relations) {
		this.name = Preconditions.checkNotNull(name);
		this.body = Preconditions.checkNotNull(body);
		this.conditions = ImmutableList.copyOf(conditions);
		this.relations = ImmutableSet.copyOf(relations);

		assert obeysConditionRestriction();
	}

	@VisibleForTesting
	static View create(String name, Atom body, List<Atom> conditions) {
		Set<Relation> relations = Sets.newHashSet();

		relations.add(body.relation());
		for (Atom condition : conditions) {
			relations.add(condition.relation());
		}

		return new View(name, body, conditions, relations);
	}

	private boolean obeysConditionRestriction() {
		Set<Term> bodyVars = body.variables();

		for (Atom atom : conditions) {
			for (Term term : atom.arguments()) {
				if (!isValidConditionTerm(bodyVars, term)) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean isValidConditionTerm(Set<Term> bodyVars, Term term) {
		TermType type = term.type();
		if (type != TermType.CONSTANT) {
			if (bodyVars.contains(term)) {
				if (type != TermType.DIST_VARIABLE) {
					return false;
				}
			} else {
				if (type != TermType.SET_VARIABLE) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Create a new view with the specified body atom and list of condition atoms. Any variable that
	 * appears in a condition atom but does not appear in the query body will be demoted to a set
	 * variable.
	 *
	 * @param name a human-readable name that is used to identify the view
	 * @param body the query's body atom
	 * @param conditions a list of atoms that will be converted to condition atoms
	 * @return the resulting view
	 */
	public static View convert(String name, Atom body, List<Atom> conditions) {
		Set<Term> bodyVars = body.variables();

		VariableDemoter demoter = new VariableDemoter(bodyVars);
		List<Atom> safeConditions = Lists.newArrayList();
		for (Atom atom : conditions) {
			safeConditions.add(atom.apply(demoter));
		}

		return create(name, body, safeConditions);
	}

	public static View convert(Atom body, List<Atom> conditions) {
		return convert("Q", body, conditions);
	}

	/**
	 * Variadic convenience method for {@link #convert}.
	 */
	public static View asView(String name, Atom body, Atom... conditions) {
		return convert(name, body, Arrays.asList(conditions));
	}

	public static View asView(Atom body, Atom... conditions) {
		return convert(body, Arrays.asList(conditions));
	}

	/**
	 * Check whether the answer to {@code otherView} uniquely determines the current view's answer
	 * on every possible dataset.
	 */
	public boolean precedes(View otherView) {
		Preconditions.checkNotNull(otherView);

		if (!relations.containsAll(otherView.relations)) {
			return false;
		}

		Specialization spec = new Specialization().extend(otherView.body, body);
		if (spec == null) {
			return false;
		}

		Conjunction source = otherView.bodyAndCondition().apply(spec);
		Conjunction target = bodyAndCondition();

		return source.findHomomorphism(target) != null;
	}

	// TODO: Unit test this method
	public View apply(TermMap map) {
		Atom newBody = body.apply(map);
		List<Atom> newConditions = Lists.newArrayListWithCapacity(conditions.size());
		for (Atom condition : conditions) {
			newConditions.add(condition.apply(map));
		}
		return convert(newBody, newConditions);
	}

	public Atom body() {
		return body;
	}

	public ImmutableList<Atom> conditions() {
		return conditions;
	}

	public String name() {
		return name;
	}

	private Conjunction bodyAndCondition() {
		List<Atom> atoms = Lists.newArrayListWithCapacity(conditions.size() + 1);
		atoms.add(body);
		atoms.addAll(conditions);
		return Conjunction.create(atoms);
	}

	/**
	 * Check whether it's possible for the current view and {@code otherView} to have any critical
	 * tuples in common. Roughly speaking, if this method returns false then the current view and
	 * {@code otherView} reveal disjoint pieces of information about the dataset. For details, see
	 * Miklau and Suciu's paper "A formal analysis of information disclosure in data exchange".
	 */
	public boolean isCompatibleWith(View otherView) {
		return body.isCompatibleWith(otherView.body);
	}

	/**
	 * Get the relation associated with the (unique) body atom of the current view.
	 */
	public Relation bodyRelation() {
		return body.relation();
	}

	/**
	 * Create a new view that is isomorphic to the callee where every variable in the callee is
	 * replaced with a fresh variable of the same type.
	 */
	public View freshCopy() {
		RefreshMap refreshMap = new RefreshMap();
		Atom newBody = body.apply(refreshMap);

		List<Atom> newConditions = Lists.newArrayListWithCapacity(conditions.size());
		for (Atom condition : conditions) {
			newConditions.add(condition.apply(refreshMap));
		}

		return create(name, newBody, newConditions);
	}

	@Override
	public int hashCode() {
		return body.hashCode() + 17 * conditions.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof View)) {
			return false;
		}
		View otherView = (View) other;
		return body.equals(otherView.body) && conditions.equals(otherView.conditions);
	}

	@Override
	public String toString() {
		return toString(ViewToStringMode.ADVANCED);
	}

	public String toString(ViewToStringMode mode) {
		return new ViewToString(name, mode)
				.appendBodyAtom(body)
				.appendAllConditionAtoms(conditions)
				.toString();
	}

	/**
	 * A term map that replaces every variable in an atom, view, or conjunction with a fresh
	 * variable of the same type.
	 */
	private static final class RefreshMap implements TermMap {
		private final Map<Term, Term> mapping;

		RefreshMap() {
			this.mapping = Maps.newHashMap();
		}

		@Override
		public Term apply(Term from) {
			if (mapping.containsKey(from)) {
				return mapping.get(from);
			}

			Term to;
			switch (from.type()) {
				case DIST_VARIABLE:
					to = Terms.dist();
					break;
				case MULTISET_VARIABLE:
					to = Terms.multiset();
					break;
				case SET_VARIABLE:
					to = Terms.set();
					break;
				case CONSTANT:
					to = from;
					break;
				case NONE:
				default:
					throw new IllegalArgumentException("Unknown term type: " + from.type());
			}

			mapping.put(from, to);
			return to;
		}
	}
}
