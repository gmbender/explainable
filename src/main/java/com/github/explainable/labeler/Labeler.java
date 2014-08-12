package com.github.explainable.labeler;

import com.github.explainable.corelang.View;

/**
 * Interface that represents a monotone map from collections of views to labels of type {@code L}.
 */
public interface Labeler<L extends Label<L>> {
	/**
	 * Compute the label for a given view.
	 *
	 * @param view the view whose label we want to compute
	 * @return an information flow label for the view
	 */
	L label(View view);

	/**
	 * Compute the label for a given sequence of views.
	 *
	 * @param views the views whose combined label we want to compute
	 * @return an information flow label for the sequence
	 */
	L label(Iterable<View> views);
}
