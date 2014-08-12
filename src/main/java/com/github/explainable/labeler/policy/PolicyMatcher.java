package com.github.explainable.labeler.policy;

import com.github.explainable.corelang.View;

/**
 * Interface used to perform pattern matching on {@link Policy} objects.
 */
public interface PolicyMatcher<T> {
	T matchFalse();

	T matchTrue();

	T matchView(View view);

	T matchAnd(Policy left, Policy right);

	T matchOr(Policy left, Policy right);
}
