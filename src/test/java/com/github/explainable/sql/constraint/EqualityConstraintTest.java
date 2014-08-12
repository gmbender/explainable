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

package com.github.explainable.sql.constraint;

import com.github.explainable.sql.ast.select.SqlPlainSelect;
import com.github.explainable.sql.table.BaseColumn;
import com.github.explainable.sql.table.BaseTable;
import com.github.explainable.sql.table.NestedScope;
import com.github.explainable.sql.table.TypedRelation;
import com.github.explainable.sql.table.TypedRelationImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.github.explainable.sql.type.TypeSystem.primitive;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link EqualityConstraint}.
 */
public class EqualityConstraintTest {
	private BaseColumn column1 = null;

	private BaseColumn column2 = null;

	@Before
	public void setUp() {
		TypedRelation relation = TypedRelationImpl.builder().setName("Rel")
				.addColumn("X", primitive())
				.addColumn("Y", primitive())
				.build();

		NestedScope scope = NestedScope.create(SqlPlainSelect.builder().build(), null);
		BaseTable table = BaseTable.create(relation, "R", scope);

		column1 = table.columns().get(0);
		column2 = table.columns().get(1);
	}

	@After
	public void tearDown() {
		column1 = null;
		column2 = null;
	}

	@Test
	public void testMatch_constant_constant() {
		final EqualityArg arg1 = ConstantArg.of("h");
		final EqualityArg arg2 = ConstantArg.of("i");
		EqualityConstraint constraint = EqualityConstraint.create(arg1, arg2);

		final IntRef callCount = new IntRef();
		constraint.match(new BaseConstraintMatcher() {
			@Override
			public void match(ConstantArg left, ConstantArg right) {
				assertEquals(arg1, left);
				assertEquals(arg2, right);
				callCount.increment();
			}
		});
		assertEquals(1, callCount.get());
	}

	@Test
	public void testMatch_constant_column() {
		final EqualityArg arg1 = ConstantArg.of("h");
		final EqualityArg arg2 = BaseColumnArg.of(column2);
		EqualityConstraint constraint = EqualityConstraint.create(arg1, arg2);

		final IntRef callCount = new IntRef();
		constraint.match(new BaseConstraintMatcher() {
			@Override
			public void match(ConstantArg left, BaseColumnArg right) {
				assertEquals(arg1, left);
				assertEquals(arg2, right);
				callCount.increment();
			}
		});
		assertEquals(1, callCount.get());
	}

	@Test
	public void testMatch_column_constant() {
		final EqualityArg arg1 = BaseColumnArg.of(column1);
		final EqualityArg arg2 = ConstantArg.of("i");
		EqualityConstraint constraint = EqualityConstraint.create(arg1, arg2);

		final IntRef callCount = new IntRef();
		constraint.match(new BaseConstraintMatcher() {
			@Override
			public void match(BaseColumnArg left, ConstantArg right) {
				assertEquals(arg1, left);
				assertEquals(arg2, right);
				callCount.increment();
			}
		});
		assertEquals(1, callCount.get());
	}

	@Test
	public void testMatch_column_column() {
		final EqualityArg arg1 = BaseColumnArg.of(column1);
		final EqualityArg arg2 = BaseColumnArg.of(column2);
		EqualityConstraint constraint = EqualityConstraint.create(arg1, arg2);

		final IntRef callCount = new IntRef();
		constraint.match(new BaseConstraintMatcher() {
			@Override
			public void match(BaseColumnArg left, BaseColumnArg right) {
				assertEquals(arg1, left);
				assertEquals(arg2, right);
				callCount.increment();
			}
		});
		assertEquals(1, callCount.get());
	}

	private abstract static class BaseConstraintMatcher implements EqualityConstraintMatcher {
		@Override
		public void match(ConstantArg left, ConstantArg right) {
			throw new AssertionError();
		}

		@Override
		public void match(ConstantArg left, BaseColumnArg right) {
			throw new AssertionError();
		}

		@Override
		public void match(BaseColumnArg left, ConstantArg right) {
			throw new AssertionError();
		}

		@Override
		public void match(BaseColumnArg left, BaseColumnArg right) {
			throw new AssertionError();
		}
	}

	private static class IntRef {
		private int value;

		IntRef() {
			this.value = 0;
		}

		void increment() {
			value++;
		}

		int get() {
			return value;
		}
	}
}
