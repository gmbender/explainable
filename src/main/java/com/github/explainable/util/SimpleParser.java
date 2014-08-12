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

import com.google.common.base.Preconditions;

/**
 * Created by gbender on 7/30/14.
 */
public abstract class SimpleParser {
	private final String input;

	private int nextIndex;

	protected SimpleParser(String input) {
		this.input = Preconditions.checkNotNull(input);
		this.nextIndex = 0;
	}

	protected char peek() {
		return (nextIndex >= input.length()) ? '\0' : input.charAt(nextIndex);
	}

	protected void advance() {
		nextIndex++;
	}

	protected boolean spaces() {
		if (!Character.isWhitespace(peek())) {
			return false;
		}

		while (Character.isWhitespace(peek())) {
			advance();
		}

		return true;
	}

	protected boolean just(char c) {
		if (peek() == c) {
			advance();
			return true;
		} else {
			return false;
		}
	}

	protected boolean except(char c) {
		if (peek() != c) {
			advance();
			return true;
		} else {
			return false;
		}
	}

	protected char letter() {
		char c = peek();
		if (Character.isLetter(c)) {
			advance();
			return c;
		} else {
			return '\0';
		}
	}

	protected char letterOrDigitOrUnderscore() {
		char c = peek();
		if (Character.isLetterOrDigit(c) || c == '_') {
			advance();
			return c;
		} else {
			return '\0';
		}
	}

	protected void match(String expected) {
		for (int i = 0; i < expected.length(); i++) {
			Preconditions.checkState(just(expected.charAt(i)));
		}
	}

	protected void eoi() {
		Preconditions.checkState(peek() == '\0');
	}

	protected void require(boolean b) {
		if (!b) {
			throw new AssertionError();
		}
	}

	protected String literal() {
		StringBuilder result = new StringBuilder();
		char next = letter();
		if (next == '\0') {
			return null;
		}

		result.append(next);
		next = letterOrDigitOrUnderscore();
		while (next != '\0') {
			result.append(next);
			next = letterOrDigitOrUnderscore();
		}

		return result.toString();
	}

	protected int digit() {
		char next = peek();
		if (Character.isDigit(next)) {
			advance();
			return next - '0';
		} else {
			return -1;
		}
	}

	protected Long integer() {
		long result = digit();
		if (result == -1) {
			return null;
		}

		int digit = digit();
		while (digit != -1) {
			result = result * 10 + digit;
			digit = digit();
		}

		return result;
	}

	protected String string() {
		if (!just('\'')) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		while (peek() != 0 && peek() != '\'') {
			builder.append(peek());
			advance();
		}
		just('\'');

		return builder.toString();
	}
}
