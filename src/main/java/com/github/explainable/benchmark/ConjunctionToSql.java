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

package com.github.explainable.benchmark;

import com.github.explainable.corelang.Atom;
import com.github.explainable.corelang.Conjunction;
import com.github.explainable.corelang.Constant;
import com.github.explainable.corelang.DistVariable;
import com.github.explainable.corelang.Relation;
import com.github.explainable.corelang.RelationImpl;
import com.github.explainable.corelang.Term;
import com.github.explainable.corelang.TermType;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import static com.github.explainable.corelang.Terms.constant;
import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.set;

/**
 * Utility class for converting a {@link Conjunction} to a human-readable SQL string.
 *
 * WARNING: This class is designed for randomized query generation, and might not preserve the
 * semantics of the original SQL query.
 */
public final class ConjunctionToSql {
	private final List<String> outerQueryTableDecls;

	private final List<String> innerQueryTableDecls;

	private final List<String> outerQueryEqualityConstraints;

	private final List<String> innerQueryEqualityConstraints;

	private final List<String> distinguished;

	private final Map<Term, String> firstRefs;

	private int tableInstanceCounter;

	ConjunctionToSql() {
		this.outerQueryTableDecls = Lists.newArrayList();
		this.innerQueryTableDecls = Lists.newArrayList();
		this.outerQueryEqualityConstraints = Lists.newArrayList();
		this.innerQueryEqualityConstraints = Lists.newArrayList();
		this.distinguished = Lists.newArrayList();
		this.firstRefs = Maps.newHashMap();
		this.tableInstanceCounter = 0;
	}

	private String freshTableInstanceName() {
		tableInstanceCounter++;
		return "R" + tableInstanceCounter;
	}

	private ConjunctionToSql addAtom(Atom atom) {
		String tableInstanceName = freshTableInstanceName();

		if (atom.getCopyVariable().type() == TermType.MULTISET_VARIABLE) {
			outerQueryTableDecls.add(atom.relation().name() + " AS " + tableInstanceName);
		} else {
			innerQueryTableDecls.add(atom.relation().name() + " AS " + tableInstanceName);
		}

		for (int i = 0; i < atom.arguments().size(); i++) {
			Term term = atom.arguments().get(i);
			String columnName = atom.relation().columnNames().get(i);
			String currentRef = tableInstanceName + "." + columnName;

			registerArgument(atom, term, currentRef);
		}

		return this;
	}

	private ConjunctionToSql addConjunction(Conjunction conjunction) {
		for (Atom atom : conjunction.atoms()) {
			addAtom(atom);
		}

		return this;
	}

	private void registerArgument(Atom atom, Term term, String currentRef) {
		String constraint = null;
		if (term instanceof Constant) {
			constraint = currentRef + " = " + term;
		} else {
			String firstRef = firstRefs.get(term);
			if (firstRef == null) {
				firstRefs.put(term, currentRef);
				if (atom.getCopyVariable().type() == TermType.MULTISET_VARIABLE
						&& term instanceof DistVariable) {
					distinguished.add(currentRef);
				}
			} else {
				constraint = firstRef + " = " + currentRef;
			}
		}

		if (constraint != null) {
			if (atom.getCopyVariable().type() == TermType.MULTISET_VARIABLE) {
				outerQueryEqualityConstraints.add(constraint);
			} else {
				innerQueryEqualityConstraints.add(constraint);
			}
		}
	}

	private void builderOuterQueryString(StringBuilder builder) {
		builder.append("SELECT ");

		if (distinguished.isEmpty()) {
			builder.append("1");
		} else {
			builder.append(Joiner.on(", ").join(distinguished));
		}

		builder.append(" FROM ");
		Joiner.on(", ").appendTo(builder, outerQueryTableDecls);

		if (!outerQueryEqualityConstraints.isEmpty() || !innerQueryTableDecls.isEmpty()) {
			builder.append(" WHERE ");

			if (!outerQueryEqualityConstraints.isEmpty()) {
				Joiner.on(" AND ").appendTo(builder, outerQueryEqualityConstraints);
			}

			if (!outerQueryEqualityConstraints.isEmpty() && !innerQueryTableDecls.isEmpty()) {
				builder.append(" AND ");
			}

			if (!innerQueryTableDecls.isEmpty()) {
				builder.append("EXISTS (");
				builderInnerQueryString(builder);
				builder.append(")");
			}
		}
	}

	private void builderInnerQueryString(StringBuilder builder) {
		builder.append("SELECT DISTINCT ");
		if (distinguished.isEmpty() || !outerQueryTableDecls.isEmpty()) {
			builder.append("1");
		} else {
			builder.append(Joiner.on(", ").join(distinguished));
		}
		builder.append(" FROM ");

		Joiner.on(", ").appendTo(builder, innerQueryTableDecls);

		if (!innerQueryEqualityConstraints.isEmpty()) {
			builder.append(" WHERE ");
			Joiner.on(" AND ").appendTo(builder, innerQueryEqualityConstraints);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (!outerQueryTableDecls.isEmpty()) {
			builderOuterQueryString(builder);
		} else if (!innerQueryTableDecls.isEmpty()) {
			builderInnerQueryString(builder);
		} else {
			builder.append("SELECT 1");
		}

		builder.append(";");

		return builder.toString();
	}

	public static String convert(Conjunction conjunction) {
		return new ConjunctionToSql().addConjunction(conjunction).toString();
	}

	public static void main(String[] args) {
		Relation user = RelationImpl.create("user", ImmutableList.of("uid", "name", "hobby"));
		Relation friend = RelationImpl.create("friend", ImmutableList.of("uid1", "uid2"));

		Term uid1 = dist();
		Term uid2 = set();
		System.out.println(new ConjunctionToSql()
				.addAtom(Atom.asMultisetAtom(user, uid2, dist(), set()))
				.addAtom(Atom.asSetAtom(friend, constant(4L), uid1))
				.addAtom(Atom.asSetAtom(friend, uid1, uid2))
				.toString());
	}
}
