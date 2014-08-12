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

package com.github.explainable.labeler.policy;

import com.github.explainable.corelang.Relation;
import com.github.explainable.corelang.View;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.List;

/**
 * Class that computes the {@link Policy} associated with a given query or set of queries.
 */
public final class PolicyLabeler {
	private final Multimap<Relation, View> hashedSecurityViews;

	private PolicyLabeler(List<View> securityViews) {
		Preconditions.checkNotNull(securityViews);

		this.hashedSecurityViews = HashMultimap.create();
		for (View securityView : securityViews) {
			this.hashedSecurityViews.put(securityView.bodyRelation(), securityView);
		}
	}

	public static PolicyLabeler create(List<View> securityViews) {
		return new PolicyLabeler(securityViews);
	}

	public Policy label(View view) {
		Policy result = Policy.FALSE;

		for (View secView : hashedSecurityViews.get(view.bodyRelation())) {
			if (view.precedes(secView)) {
				result = result.or(Policy.of(secView));
			}
		}

		return result;
	}

	public Policy label(Iterable<View> views) {
		Policy result = Policy.TRUE;

		for (View view : views) {
			result = result.and(label(view));
		}

		return result;
	}
}
