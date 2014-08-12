package com.github.explainable.util;

import com.github.explainable.corelang.View;
import com.github.explainable.sql.type.PrimitiveType;
import com.github.explainable.sql.type.RowCount;
import com.github.explainable.sql.type.SchemaTableType;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Additional assertions for use in unit tests.
 */
public final class MoreAsserts {
	private MoreAsserts() {
		throw new UnsupportedOperationException("Can't instantiate MoreAsserts");
	}

	/**
	 * Assert that {@code collection} consists of the specified elements. This method ignores order but
	 * is sensitive to duplicate elements. The order of the arguments in this method is reversed
	 * compared to most assertion methods: The collection we want to check comes <b>before</b> the list
	 * of expected arguments.
	 *
	 * @param collection The collection of elements we want to check
	 * @param elements The elements that are expected to comprise the collection
	 */
	public static <T> void assertElementsAnyOrder(Iterable<T> collection, T... elements) {
		assertEquals(HashMultiset.create(collection), HashMultiset.create(Arrays.asList(elements)));
	}

	/**
	 * Verify that two single-atom views reveal equivalent information about the underlying dataset.
	 * Formally, we require that {@code expected.precedes(actual)} and {@code
	 * actual.precedes(expected)} must both hold.
	 */
	public static void assertEquivalent(View expected, View actual) {
		if (!expected.precedes(actual) || !actual.precedes(expected)) {
			fail("expected:<" + expected + ">" + " but was:<" + actual + ">");
		}
	}

	/**
	 * Assert that {@code actualViews} and {@code expectedViews} contain the same number of elements,
	 * and that for each index i, the ith element of {@code actualViews} and the ith element of {@code
	 * expectedViews} are equivalent (i.e., {@link View#precedes(View)} holds in both directions. The
	 * order of the arguments in this method is reversed compared to most assertion methods: The
	 * collection we want to check comes <b>before</b> the list of expected arguments.
	 *
	 * @param actualViews The atoms that we observed
	 * @param expectedViews The atoms that we expected to see
	 */
	public static void assertEquivalentElements(List<View> actualViews, View... expectedViews) {
		assertEquals("Actual number of atoms (" + actualViews.size() + ")"
				+ " is different from expected number of atoms (" + expectedViews.length + ")",
				expectedViews.length, actualViews.size());

		for (int i = 0; i < actualViews.size(); i++) {
			assertEquivalent(expectedViews[i], actualViews.get(i));
		}
	}

	public static void assertSchemaTableType(
			SchemaTableType actualType,
			RowCount expectedRows,
			PrimitiveType... expectedColumnTypes) {
		ImmutableList<PrimitiveType> columnTypeList = ImmutableList.copyOf(expectedColumnTypes);
		assertEquals(TypeSystem.schemaTable(expectedRows, columnTypeList), actualType);
	}
}
