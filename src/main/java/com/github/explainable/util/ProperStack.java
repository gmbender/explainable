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
