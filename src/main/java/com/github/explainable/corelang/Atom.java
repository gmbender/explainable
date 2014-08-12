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

import com.github.explainable.util.UnionFindNode;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class representing an atom in a conjunctive query.
 */
public final class Atom {
	private final Relation relation;

	private final ImmutableList<Term> arguments;

	private final Term copyVariable;

	private Atom(Relation relation, List<? extends Term> arguments, Term copyVariable) {
		Preconditions.checkArgument(
				relation.arity() == arguments.size(),
				"View has wrong number of arguments");

		this.relation = Preconditions.checkNotNull(relation);
		this.arguments = ImmutableList.copyOf(arguments);
		this.copyVariable = Preconditions.checkNotNull(copyVariable);
	}

	public static Atom createSetAtom(Relation relation, List<? extends Term> arguments) {
		return new Atom(relation, arguments, Terms.set());
	}

	public static Atom createMultisetAtom(Relation relation, List<? extends Term> arguments) {
		return new Atom(relation, arguments, Terms.multiset());
	}

	public static Atom asSetAtom(Relation relation, Term... arguments) {
		return new Atom(relation, ImmutableList.copyOf(arguments), Terms.set());
	}

	public static Atom asMultisetAtom(Relation relation, Term... arguments) {
		return new Atom(relation, ImmutableList.copyOf(arguments), Terms.multiset());
	}

	public Relation relation() {
		return relation;
	}

	public Term getCopyVariable() {
		return copyVariable;
	}

	public ImmutableList<Term> arguments() {
		return arguments;
	}

	Set<Term> variables() {
		Set<Term> result = Sets.newHashSet();

		for (Term term : arguments) {
			if (term.type() != TermType.CONSTANT) {
				result.add(term);
			}
		}

		return result;
	}

	public Atom apply(TermMap termMap) {
		List<Term> newTerms = Lists.newArrayListWithCapacity(arguments.size());

		for (Term term : arguments) {
			newTerms.add(termMap.apply(term));
		}

		return new Atom(relation, newTerms, termMap.apply(copyVariable));
	}

	/**
	 * Given atoms {@code R(x, y, ...)} and {@code }R(x', y', ...)}, compute a new atom in which the
	 * ith argument of {@code R(x, y, ...)} is unified with the ith argument of {@code R(x', y',
	 *...)}. The result corresponds to the expression {@code (x, y, ...) ^ (x = x') ^ (y = y') ^
	 * ...}. This process is closely related to the problem of finding MGUs in Datalog.
	 *
	 * Warning: We CANNOT guarantee that the resulting atom reveals less information about the
	 * dataset than either of the input atoms.
	 *
	 * @param other the atom to unify with
	 * @return the unifier of the two atoms
	 */
	@Nullable
	public Atom unifyWith(Atom other) {
		if (!relation().equals(other.relation())) {
			return null;
		}

		Preconditions.checkArgument(arguments.size() == other.arguments.size());
		Map<Term, UnionFindNode<Term>> termNodes = unionFindsForAllTerms();
		Map<Term, UnionFindNode<Term>> otherTermNodes = other.unionFindsForAllTerms();

		for (int i = 0; i < arguments.size(); i++) {
			UnionFindNode<Term> termNode = termNodes.get(arguments.get(i));
			UnionFindNode<Term> otherTermNode = otherTermNodes.get(other.arguments.get(i));

			Term unifier = termNode.get().unifyWith(otherTermNode.get());
			if (unifier == null) {
				return null;
			}

			termNode.set(unifier);
			otherTermNode.set(unifier);
			Preconditions.checkArgument(termNode.mergeWith(otherTermNode));
		}

		List<Term> unifiedArguments = Lists.newArrayListWithCapacity(arguments.size());
		for (Term term : arguments) {
			unifiedArguments.add(termNodes.get(term).get());
		}

		Term unifiedCopyVariable = getCopyVariable().unifyWith(other.getCopyVariable());
		return new Atom(relation(), unifiedArguments, unifiedCopyVariable);
	}

	/**
	 * Determine whether {@code this} and {@code otherView} have any critical tuples in common.
	 *
	 * @param other the atom that we want to compare against
	 * @return {@code true} if {@code this} and {@code otherView} have at least one critical tuple
	 * in common
	 */
	public boolean isCompatibleWith(Atom other) {
		return unifyWith(other) != null;
	}

	private Map<Term, UnionFindNode<Term>> unionFindsForAllTerms() {
		Map<Term, UnionFindNode<Term>> termNodes = Maps.newHashMap();

		for (Term term : arguments) {
			termNodes.put(term, UnionFindNode.create(term));
		}

		return termNodes;
	}

	@Override
	public int hashCode() {
		return relation.hashCode() + 17 * arguments.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (!(other instanceof Atom)) {
			return false;
		}
		Atom otherAtom = (Atom) other;
		return relation.equals(otherAtom.relation()) && arguments.equals(otherAtom.arguments());
	}

	@Override
	public String toString() {
		return relation.name()
				+ "("
				+ Joiner.on(", ").join(arguments)
				+ "; "
				+ copyVariable
				+ ")";
	}
}
