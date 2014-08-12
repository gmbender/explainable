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

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class that's used to extract an atom from the representation that we use internally into a
 * human-readable string that's very similar to the format used in Sara Cohen's combined
 * set/multiset semantics.
 */
final class ViewToString {
	private static final int MAX_COLUMN_INSTANCES = 100;

	private final String viewName;

	private final ViewToStringMode mode;

	private final Map<Term, String> distinctTermNames;

	private final List<String> headTermNames;

	private final List<String> multisetVariableNames;

	private final List<String> bodyAtomNames;

	private final List<String> conditionAtomNames;

	private final Set<String> variableNames;

	ViewToString(String viewName, ViewToStringMode mode) {
		this.viewName = Preconditions.checkNotNull(viewName);
		this.mode = Preconditions.checkNotNull(mode);
		this.distinctTermNames = Maps.newHashMap();

		this.headTermNames = Lists.newArrayList();
		this.multisetVariableNames = Lists.newArrayList();
		this.bodyAtomNames = Lists.newArrayList();
		this.conditionAtomNames = Lists.newArrayList();
		this.variableNames = Sets.newHashSet();
	}

	private String freshVariableName(String columnName) {
		Preconditions.checkArgument(!columnName.isEmpty());
		boolean endsWithDigit = Character.isDigit(columnName.charAt(columnName.length() - 1));

		if (variableNames.add(columnName)) {
			return columnName;
		}

		for (int counter = 2; counter <= MAX_COLUMN_INSTANCES; counter++) {
			String candidate = endsWithDigit
					? (columnName + "_" + counter)
					: (columnName + counter);

			if (variableNames.add(candidate)) {
				return candidate;
			}
		}

		throw new UnsupportedOperationException(
				"There are too many variables with the same column name: " + columnName);
	}

	private List<String> atomArgsToStringList(Atom atom) {
		List<Term> terms = atom.arguments();
		List<String> columnNames = atom.relation().columnNames();
		Preconditions.checkArgument(terms.size() == columnNames.size());

		List<String> argNames = Lists.newArrayListWithCapacity(terms.size());

		for (int i = 0; i < columnNames.size(); i++) {
			Term term = terms.get(i);
			String columnName = columnNames.get(i);
			registerTerm(columnName, term);
			argNames.add(distinctTermNames.get(term));
		}

		return argNames;
	}

	private void registerTerm(String columnName, Term term) {
		if (!distinctTermNames.containsKey(term)) {
			TermType termType = term.type();
			switch (termType) {
				case DIST_VARIABLE:
					distinctTermNames.put(term, columnName);
					headTermNames.add(freshVariableName(columnName));
					break;
				case MULTISET_VARIABLE:
					distinctTermNames.put(term, columnName);
					multisetVariableNames.add(freshVariableName(columnName));
					break;
				case SET_VARIABLE:
					distinctTermNames.put(term, freshVariableName(columnName));
					break;
				case CONSTANT:
					distinctTermNames.put(term, term.toString());
					break;
				case NONE: // Fall-through
				default:
					throw new IllegalArgumentException("Unknown term type: " + termType);
			}
		}
	}

	private String atomToString(Atom atom) {
		StringBuilder resultBuilder = new StringBuilder();
		resultBuilder.append(atom.relation().name());
		resultBuilder.append("(");
		Joiner.on(", ").appendTo(resultBuilder, atomArgsToStringList(atom));

		if (mode == ViewToStringMode.ADVANCED) {
			Term copyVariable = atom.getCopyVariable();
			registerTerm("i", copyVariable);
			resultBuilder.append("; ");
			resultBuilder.append(distinctTermNames.get(copyVariable));
		}

		resultBuilder.append(")");
		return resultBuilder.toString();
	}

	ViewToString appendBodyAtom(Atom atom) {
		bodyAtomNames.add(atomToString(atom));
		return this;
	}

	ViewToString appendAllBodyAtoms(List<Atom> atoms) {
		for (Atom atom : atoms) {
			appendBodyAtom(atom);
		}

		return this;
	}

	ViewToString appendConditionAtom(Atom atom) {
		conditionAtomNames.add(atomToString(atom));
		return this;
	}

	ViewToString appendAllConditionAtoms(List<Atom> atoms) {
		for (Atom atom : atoms) {
			appendConditionAtom(atom);
		}

		return this;
	}

	void appendHeadTo(StringBuilder accumulator) {
		accumulator.append(viewName);
		accumulator.append("(");
		Joiner.on(", ").appendTo(accumulator, headTermNames);
		accumulator.append(")");
	}

	void appendColonMinusTo(StringBuilder accumulator) {
		accumulator.append(" :- ");
	}

	void appendBodyAtomsTo(StringBuilder accumulator) {
		Joiner.on(", ").appendTo(accumulator, bodyAtomNames);
	}

	void appendMultisetTo(StringBuilder accumulator) {
		if (multisetVariableNames.isEmpty()) {
			accumulator.append(" {}");
		} else {
			accumulator.append(" { ");
			Joiner.on(", ").appendTo(accumulator, multisetVariableNames);
			accumulator.append(" }");
		}
	}

	void appendConditionAtomsTo(StringBuilder accumulator) {
		accumulator.append(" \u22c9 ");
		Joiner.on(", ").appendTo(accumulator, conditionAtomNames);
	}

	boolean hasConditionAtoms() {
		return !conditionAtomNames.isEmpty();
	}

	// Subclasses must override this method
	@Override
	public String toString() {
		StringBuilder resultBuilder = new StringBuilder();
		appendHeadTo(resultBuilder);
		appendColonMinusTo(resultBuilder);
		appendBodyAtomsTo(resultBuilder);

		if (hasConditionAtoms()) {
			appendConditionAtomsTo(resultBuilder);
		}

		if (mode == ViewToStringMode.ADVANCED) {
			appendMultisetTo(resultBuilder);
		}

		return resultBuilder.toString();
	}
}
