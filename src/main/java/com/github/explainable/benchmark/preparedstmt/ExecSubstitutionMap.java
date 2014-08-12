package com.github.explainable.benchmark.preparedstmt;

import com.github.explainable.corelang.Term;
import com.github.explainable.corelang.TermMap;
import com.github.explainable.corelang.Terms;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Given a list of arguments (T1, T2, ..., Tn), map the constant '$1' to T1, the constant '$2' to
 * T2, and so on.
 */
final class ExecSubstitutionMap implements TermMap {
	private final ImmutableMap<Term, Term> substitutions;

	private ExecSubstitutionMap(Map<Term, Term> substitutions) {
		this.substitutions = ImmutableMap.copyOf(substitutions);
	}

	public static ExecSubstitutionMap create(List<Term> arguments) {
		Map<Term, Term> substitutions = Maps.newHashMap();
		for (int i = 0; i < arguments.size(); i++) {
			substitutions.put(Terms.constant("$" + (i + 1)), arguments.get(i));
		}
		return new ExecSubstitutionMap(substitutions);
	}

	@Override
	public Term apply(Term from) {
		Term to = substitutions.get(from);
		return (to == null) ? from : to;
	}

	@Override
	public String toString() {
		return substitutions.toString();
	}
}
