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
import com.github.explainable.labeler.AbstractLabeler;
import com.github.explainable.labeler.Label;
import com.google.common.collect.ImmutableList;

/**
 * Algorithm that computes an Aeolus-style information flow label for a given database query or
 * mutation.
 */
public final class AeolusLabeler<L extends Label<L>> extends AbstractLabeler<L> {
	/**
	 * The set of security views that are used to compute disclosure labels, together with their
	 * corresponding labels.
	 */
	private final ImmutableList<AeolusRule<L>> rules;

	/**
	 * Create a new disclosure labeler with the specified list of views and info flow labels.
	 *
	 * @param labeledViews A list of (security view, information flow label) pairs
	 */
	private AeolusLabeler(L top, L bottom, Iterable<AeolusRule<L>> labeledViews) {
		super(top, bottom);
		this.rules = ImmutableList.copyOf(labeledViews);
	}

	public static <L extends Label<L>>
	AeolusLabeler<L> create(L top, L bottom, Iterable<AeolusRule<L>> labeledViews) {
		return new AeolusLabeler<L>(top, bottom, labeledViews);
	}

	@Override
	public L label(View view) {
		L currentLabel = top();

		for (AeolusRule<L> aeolusRule : rules) {
			if (view.precedes(aeolusRule.view())) {
				currentLabel = currentLabel.greatestLowerBound(aeolusRule.label());
			}
		}

		return currentLabel;
	}
}
