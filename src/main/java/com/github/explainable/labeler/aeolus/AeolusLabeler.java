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
