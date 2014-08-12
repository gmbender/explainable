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

package com.github.explainable.sql.type;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link PrimitiveBottomTypeImpl}.
 */
public class NullTypeImplTest {
	private BoolTypeImpl boolType;

	private PrimitiveBottomTypeImpl nullType;

	private NumericTypeImpl numericType;

	private PrimitiveTypeImpl primitiveType;

	private StringTypeImpl stringType;

	@Before
	public void setUp() {
		boolType = new BoolTypeImpl();
		nullType = new PrimitiveBottomTypeImpl();
		numericType = new NumericTypeImpl();
		primitiveType = new PrimitiveTypeImpl();
		stringType = new StringTypeImpl();
	}

	@Test
	public void testIsSupertypeOf() throws Exception {
		assertFalse(nullType.isSupertypeOf(boolType));
		assertTrue(nullType.isSupertypeOf(new PrimitiveBottomTypeImpl()));
		assertFalse(nullType.isSupertypeOf(numericType));
		assertFalse(nullType.isSupertypeOf(primitiveType));
		assertFalse(nullType.isSupertypeOf(stringType));
	}

	@Test
	public void testEquals() throws Exception {
		// We avoid calling assertEquals() because we're not yet confident that our implementation
		// of equals(...) is symmetric, and it's undocumented whether assertEquals(X, Y) checks
		// X.equals(Y), Y.equals(X), or both.
		assertTrue(nullType.equals(new PrimitiveBottomTypeImpl()));
		assertFalse(nullType.equals(primitiveType));
	}

	@Test
	public void testToBoolType() throws Exception {
		assertEquals(nullType, nullType.coerceToBool());
	}

	@Test
	public void testToNumericType() throws Exception {
		assertEquals(nullType, nullType.coerceToNumeric());
	}

	@Test
	public void testToStringType() throws Exception {
		assertEquals(nullType, nullType.coerceToString());
	}

	@Test
	public void testToPrimitiveType() throws Exception {
		assertEquals(nullType, nullType.coerceToPrimitive());
	}

	@Test
	public void testToTableType() throws Exception {
		assertNull(nullType.coerceToTable());
	}

	@Test
	public void testToSchemaTableType() throws Exception {
		assertNull(nullType.coerceToSchemaTable());
	}

	@Test
	public void testToSchemaListType() throws Exception {
		assertNull(nullType.coerceToSchemaList());
	}

	@Test
	public void testGetCommonSupertype() throws Exception {
		assertEquals(boolType, nullType.commonSupertype(boolType));
		assertEquals(nullType, nullType.commonSupertype(new PrimitiveBottomTypeImpl()));
		assertEquals(numericType, nullType.commonSupertype(numericType));
		assertEquals(primitiveType, nullType.commonSupertype(primitiveType));
		assertEquals(stringType, nullType.commonSupertype(stringType));
	}

	@Test
	public void testUnifyWith() throws Exception {
		assertEquals(nullType, nullType.unifyWith(boolType));
		assertEquals(nullType, nullType.unifyWith(new PrimitiveBottomTypeImpl()));
		assertEquals(nullType, nullType.unifyWith(numericType));
		assertEquals(nullType, nullType.unifyWith(primitiveType));
		assertEquals(nullType, nullType.unifyWith(stringType));
	}
}
