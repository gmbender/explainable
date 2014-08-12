package com.github.explainable.corelang;

/**
 * Interface which represents a function that maps terms to terms.
 */
public interface TermMap {
	Term apply(Term from);
}
