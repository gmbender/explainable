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
