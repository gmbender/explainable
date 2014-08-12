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
