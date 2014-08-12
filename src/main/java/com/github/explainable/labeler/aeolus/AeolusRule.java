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

package com.github.explainable.labeler.aeolus;

import com.github.explainable.corelang.View;
import com.github.explainable.labeler.Label;

public final class AeolusRule<L extends Label<L>> {
	private final View view;

	private final L label;

	private AeolusRule(View view, L label) {
		this.view = view;
		this.label = label;
	}

	public static <L extends Label<L>>
	AeolusRule<L> create(View view, L label) {
		return new AeolusRule<L>(view, label);
	}

	public View view() {
		return view;
	}

	public L label() {
		return label;
	}

	@Override
	public int hashCode() {
		return view.hashCode() + 17 * label.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AeolusRule) {
			AeolusRule<?> otherView = (AeolusRule<?>) other;
			return view.equals(otherView.view) && label.equals(otherView.label);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return view + " with label " + label;
	}
}
