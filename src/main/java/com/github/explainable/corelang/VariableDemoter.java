package com.github.explainable.corelang;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

/**
 * Map that converts an ordinary query atom into a condition atom. Every constant and every term
 * that appears in the query body is mapped to itself. All other terms are demoted to fresh
 * distinguished variables.
 */
final class VariableDemoter implements TermMap {
	private final Map<Term, Term> forwardMap;

	VariableDemoter(Set<? extends Term> bodyTerms) {
		forwardMap = Maps.newHashMap();
		for (Term term : bodyTerms) {
			forwardMap.put(term, term);
		}
	}

	@Override
	public Term apply(Term from) {
		if (from.type() == TermType.CONSTANT) {
			return from;
		} else {
			Term to = forwardMap.get(from);

			if (to == null) {
				to = Terms.set();
				forwardMap.put(from, to);
			}

			return to;
		}
	}
}
