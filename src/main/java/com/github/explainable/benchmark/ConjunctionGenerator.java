package com.github.explainable.benchmark;

import com.github.explainable.corelang.Atom;
import com.github.explainable.corelang.Conjunction;
import com.github.explainable.corelang.Relation;
import com.github.explainable.corelang.Term;
import com.github.explainable.corelang.TermMap;
import com.github.explainable.corelang.TermType;
import com.github.explainable.corelang.Terms;
import com.github.explainable.corelang.View;
import com.github.explainable.util.RandomSampler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Class that randomly generates conjunctive queries by recursively selecting and applying templates
 * from a specified list.
 */
public final class ConjunctionGenerator {
	private static final int MAX_OUTPUT_SIZE = 10;

	private final ImmutableList<View> templates;

	private final RandomSampler sampler;

	private ConjunctionGenerator(List<View> templates, Random random) {
		Preconditions.checkArgument(!templates.isEmpty());
		this.templates = ImmutableList.copyOf(templates);
		this.sampler = RandomSampler.create(random);
	}

	public static ConjunctionGenerator create(List<View> templates, Random random) {
		return new ConjunctionGenerator(templates, random);
	}

	public static ConjunctionGenerator createAndSeed(List<View> templates) {
		return new ConjunctionGenerator(templates, new Random());
	}

	/**
	 * Output a random conjunction with distinguished variables selected randomly from the first
	 * atom of the query. All distinguished variables must appear in the first body atom of the
	 * query. Every distinguished variable and every set-existential variable in the query will be
	 * referenced by the first body atom.
	 *
	 * @param lambda parameter of the Poisson distribution from which the number of distinguished
	 * variables is chosen
	 */
	public Conjunction nextWithRandomDist(double lambda) {
		Conjunction result = nextWithNoDist();
		if (result == null || result.atoms().isEmpty()) {
			return result;
		}

		Atom firstAtom = result.atoms().get(0);
		if (firstAtom.arguments().isEmpty()) {
			return result;
		}

		int numDist = sampler.nextPoisson(lambda, firstAtom.arguments().size());
		List<Term> distArguments = sampler.sample(firstAtom.arguments(), numDist);

		return result.apply(new DistVariablePromoter(distArguments));
	}

	/**
	 * Output a random conjunction without any distinguished variables. Every distinguished variable
	 * and every set-existential variable in the query will be referenced by the first body atom.
	 */
	@Nullable
	@VisibleForTesting
	Conjunction nextWithNoDist() {
		Conjunction result = nextWithDefaultDist();
		if (result != null) {
			result = result.apply(new DistVariableRemover());
		}
		return result;
	}

	/**
	 * Output a random conjunction whose distinguished variables are selected based on the input
	 * templates that are used to generate the conjunction. Every distinguished variable and every
	 * set-existential variable in the query will be referenced by the first body atom.
	 */
	@Nullable
	@VisibleForTesting
	Conjunction nextWithDefaultDist() {
		List<Atom> atoms = Lists.newArrayList();

		View seedView = sampler.choice(templates).freshCopy();
		atoms.add(seedView.body());
		atoms.addAll(seedView.conditions());

		Set<Relation> exploredRelations = Sets.newHashSet(seedView.body().relation());

		for (int i = 1; i < atoms.size(); i++) {
			if (i > MAX_OUTPUT_SIZE) {
				throw new IllegalStateException("Output exceeds maximum permitted size");
			}

			if (!exploredRelations.contains(atoms.get(i).relation())) {
				exploredRelations.add(atoms.get(i).relation());

				if (!extendAtomList(atoms, i)) {
					return null;
				}
			}
		}

		return Conjunction.create(atoms);
	}

	private boolean extendAtomList(List<Atom> atoms, int i) {
		List<View> candidates = Lists.newArrayListWithCapacity(templates.size());
		for (View template : templates) {
			if (atoms.get(i).isCompatibleWith(template.body())) {
				candidates.add(template);
			}
		}

		if (!candidates.isEmpty()) {
			View selected = sampler.choice(candidates).freshCopy();
			Atom unifier = atoms.get(i).unifyWith(selected.body());
			if (unifier == null) {
				return false;
			}

			UnifierMap atomsToUnifier = new UnifierMap(atoms.get(i), unifier);
			for (int j = 0; j < atoms.size(); j++) {
				atoms.set(j, atoms.get(j).apply(atomsToUnifier));
			}

			UnifierMap selectedToUnifier = new UnifierMap(selected.body(), unifier);
			for (Atom selectedCond : selected.conditions()) {
				atoms.add(selectedCond.apply(selectedToUnifier));
			}
		}

		return true;
	}

	/**
	 * A map that sends terms from one atom to another atom that is defined on the same relation. In
	 * general, we expect the second atom to be computed by calling {@link Atom#unifyWith(Atom)},
	 * where the first atom is either the method's callee or its argument.
	 */
	private static final class UnifierMap implements TermMap {
		Map<Term, Term> mapping;

		UnifierMap(Atom original, Atom unifier) {
			Preconditions.checkArgument(original.relation().equals(unifier.relation()));
			mapping = Maps.newHashMap();

			List<Term> originalArgs = original.arguments();
			List<Term> unifierArgs = unifier.arguments();
			for (int i = 0; i < originalArgs.size(); i++) {
				mapping.put(originalArgs.get(i), unifierArgs.get(i));
			}
		}

		@Override
		public Term apply(Term from) {
			Term to = mapping.get(from);
			return (to != null) ? to : from;
		}
	}

	/**
	 * A map that transforms all distinguished variables into fresh set-existential variables and
	 * keeps all remaining variables the same.
	 */
	private static final class DistVariableRemover implements TermMap {
		Map<Term, Term> mapping;

		DistVariableRemover() {
			mapping = Maps.newHashMap();
		}

		@Override
		public Term apply(Term from) {
			Term to = mapping.get(from);
			if (to == null) {
				to = (from.type() == TermType.DIST_VARIABLE) ? Terms.set() : from;
				mapping.put(from, to);
			}
			return to;
		}
	}

	private static final class DistVariablePromoter implements TermMap {
		Map<Term, Term> mapping;

		DistVariablePromoter(List<Term> varsToPromote) {
			mapping = Maps.newHashMap();

			for (Term term : varsToPromote) {
				mapping.put(term, term.unifyWith(Terms.dist()));
			}
		}

		@Override
		public Term apply(Term from) {
			Term to = mapping.get(from);
			return (to != null) ? to : from;
		}
	}
}
