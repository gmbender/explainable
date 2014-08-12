package com.github.explainable.labeler;

/**
 * Generic interface representing an immutable point in an information flow lattice (as in Aeolus or
 * Flume) or a permission lattice (e.g., as in Facebook's permission structure).
 */
public interface Label<L> {
	/**
	 * Compare two points under the natural lattice order.
	 */
	boolean precedes(L other);

	/**
	 * Compute the least upper bound of two points in the lattice.
	 */
	L leastUpperBound(L other);

	/**
	 * Compute the greatest lower bound of two points in the lattice.
	 */
	L greatestLowerBound(L other);
}
