package com.github.explainable.util;

import java.util.EmptyStackException;
import java.util.Iterator;

/**
 * The stack implementation in {@link java.util.Stack} extends from {@link java.util.Vector}, which
 * causes problems for element iteration. Elements in {@link java.util.Stack} are iterated from the
 * bottom of the stack to the top, which is the opposite order from what you'd usually want. This
 * implementation fixes the iteration problem. It is not thread-safe, and will probably break if you
 * modify the stack while you're iterating through it.
 */
public interface ProperStack<T> extends Iterable<T> {
	boolean isEmpty();

	/**
	 * Retrieve the top element in the stack.
	 *
	 * @return the top element in the stack
	 * @throws EmptyStackException if the stack doesn't have any elements
	 */
	T peek();

	/**
	 * Remove the top element in the stack.
	 *
	 * @return the element that was previously on the top of the stack
	 * @throws EmptyStackException if the stack doesn't have any elements
	 */
	T pop();

	/**
	 * Push a new element onto the stack.
	 *
	 * @param element the element that should be put on the top of the stack
	 * @return a reference to this object
	 */
	ProperStack<T> push(T element);

	/**
	 * Iterate through the elements in the stack, starting at the top of the stack and ending at the
	 * bottom.
	 *
	 * @return an iterator for this object
	 */
	@Override
	Iterator<T> iterator();
}
