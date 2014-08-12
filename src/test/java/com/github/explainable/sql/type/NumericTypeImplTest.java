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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/23/13 Time: 2:31 PM To change this template use
 * File | Settings | File Templates.
 */
public class NumericTypeImplTest {
	private PrimitiveBottomTypeImpl nullType = null;

	private NumericTypeImpl numericType = null;

	private PrimitiveTypeImpl primitiveType = null;

	@Before
	public void setUp() {
		nullType = new PrimitiveBottomTypeImpl();
		numericType = new NumericTypeImpl();
		primitiveType = new PrimitiveTypeImpl();
	}

	@After
	public void tearDown() {
		nullType = null;
		numericType = null;
		primitiveType = null;
	}

	@Test
	public void testIsSupertypeOf() throws Exception {
		assertTrue(numericType.isSupertypeOf(new NumericTypeImpl()));
		assertTrue(numericType.isSupertypeOf(nullType));
		assertFalse(numericType.isSupertypeOf(primitiveType));
		assertFalse(numericType.isSupertypeOf(new TableTypeImpl()));
	}

	@Test
	public void testEquals() throws Exception {
		// We avoid calling assertEquals() because we're not yet confident that our implementation
		// of equals(...) is symmetric, and it's undocumented whether assertEquals(X, Y) checks
		// X.equals(Y), Y.equals(X), or both.
		assertFalse(numericType.equals(nullType));
		assertTrue(numericType.equals(numericType));
		assertFalse(numericType.equals(primitiveType));
	}

	@Test
	public void testToBoolType() throws Exception {
		assertNull(numericType.coerceToBool());
	}

	@Test
	public void testToNumericType() throws Exception {
		assertEquals(numericType, numericType.coerceToNumeric());
	}

	@Test
	public void testToStringType() throws Exception {
		assertNull(numericType.coerceToString());
	}

	@Test
	public void testToPrimitiveType() throws Exception {
		assertEquals(numericType, numericType.coerceToPrimitive());
	}

	@Test
	public void testToTableType() throws Exception {
		assertNull(numericType.coerceToTable());
	}

	@Test
	public void testToSchemaTableType() throws Exception {
		assertNull(numericType.coerceToSchemaTable());
	}

	@Test
	public void testToSchemaListType() throws Exception {
		assertNull(numericType.coerceToSchemaList());
	}

	@Test
	public void testGetCommonSupertype() throws Exception {
		assertEquals(numericType, numericType.commonSupertype(new NumericTypeImpl()));
		assertEquals(numericType, numericType.commonSupertype(nullType));
		assertEquals(primitiveType, numericType.commonSupertype(primitiveType));
		assertEquals(primitiveType, numericType.commonSupertype(new StringTypeImpl()));
	}

	@Test
	public void testUnifyWith() throws Exception {
		assertEquals(numericType, numericType.unifyWith(new NumericTypeImpl()));
		assertEquals(nullType, numericType.unifyWith(nullType));
		assertEquals(numericType, numericType.unifyWith(primitiveType));
		assertNull(numericType.unifyWith(new StringTypeImpl()));
	}
}
