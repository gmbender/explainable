package com.github.explainable.corelang;

import com.google.common.collect.ImmutableList;

/**
 * Class that represents a database relation in our core query language.
 */
public interface Relation {
	/**
	 * Get a human-readable name for the current relation.
	 */
	String name();

	/**
	 * Determine the number of columns in the current relation.
	 */
	int arity();

	/**
	 * Get the names of the current relation's columns.
	 */
	ImmutableList<String> columnNames();
}
