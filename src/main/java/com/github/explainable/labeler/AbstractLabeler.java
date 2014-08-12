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

package com.github.explainable.labeler;

import com.github.explainable.corelang.View;
import com.google.common.base.Preconditions;

/**
 * Skeletal implementation of {@link Labeler}. We deliberately avoid implementing {@link
 * Labeler#label(View)}, since the implementation tends to vary significantly from one class to the
 * next.
 */
public abstract class AbstractLabeler<L extends Label<L>> implements Labeler<L> {
	private final L top;

	private final L bottom;

	protected AbstractLabeler(L top, L bottom) {
		Preconditions.checkArgument(bottom.precedes(top));
		this.top = Preconditions.checkNotNull(top);
		this.bottom = Preconditions.checkNotNull(bottom);
	}

	protected final L top() {
		return top;
	}

	protected final L bottom() {
		return bottom;
	}

	@Override
	public final L label(Iterable<View> views) {
		L currentLabel = bottom;

		for (View view : views) {
			currentLabel = currentLabel.leastUpperBound(label(view));
		}

		return currentLabel;
	}
}
