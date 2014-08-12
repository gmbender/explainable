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
