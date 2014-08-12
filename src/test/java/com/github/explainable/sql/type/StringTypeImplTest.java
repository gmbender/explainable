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
 * Created with IntelliJ IDEA. User: gbender Date: 9/23/13 Time: 3:18 PM To change this template use
 * File | Settings | File Templates.
 */
public class StringTypeImplTest {
	private PrimitiveBottomTypeImpl nullType = null;

	private PrimitiveTypeImpl primitiveType = null;

	private StringTypeImpl stringType = null;

	@Before
	public void setUp() {
		nullType = new PrimitiveBottomTypeImpl();
		primitiveType = new PrimitiveTypeImpl();
		stringType = new StringTypeImpl();
	}

	@After
	public void tearDown() {
		nullType = null;
		primitiveType = null;
		stringType = null;
	}

	@Test
	public void testIsSupertypeOf() throws Exception {
		assertTrue(stringType.isSupertypeOf(nullType));
		assertFalse(stringType.isSupertypeOf(primitiveType));
		assertTrue(stringType.isSupertypeOf(new StringTypeImpl()));
		assertFalse(stringType.isSupertypeOf(new TableTypeImpl()));
	}

	@Test
	public void testEquals() throws Exception {
		// We avoid calling assertEquals() because we're not yet confident that our implementation
		// of equals(...) is symmetric, and it's undocumented whether assertEquals(X, Y) checks
		// X.equals(Y), Y.equals(X), or both.
		assertFalse(stringType.equals(nullType));
		assertFalse(stringType.equals(primitiveType));
		assertTrue(stringType.equals(new StringTypeImpl()));
	}

	@Test
	public void testToBoolType() throws Exception {
		assertNull(stringType.coerceToBool());
	}

	@Test
	public void testToNumericType() throws Exception {
		assertNull(stringType.coerceToNumeric());
	}

	@Test
	public void testToStringType() throws Exception {
		assertEquals(stringType, stringType.coerceToString());
	}

	@Test
	public void testToPrimitiveType() throws Exception {
		assertEquals(stringType, stringType.coerceToPrimitive());
	}

	@Test
	public void testToTableType() throws Exception {
		assertNull(stringType.coerceToTable());
	}

	@Test
	public void testToSchemaTableType() throws Exception {
		assertNull(stringType.coerceToSchemaTable());
	}

	@Test
	public void testToSchemaListType() throws Exception {
		assertNull(stringType.coerceToSchemaList());
	}

	@Test
	public void testGetCommonSupertype() throws Exception {
		assertEquals(primitiveType, stringType.commonSupertype(new BoolTypeImpl()));
		assertEquals(stringType, stringType.commonSupertype(nullType));
		assertEquals(primitiveType, stringType.commonSupertype(primitiveType));
		assertEquals(stringType, stringType.commonSupertype(new StringTypeImpl()));
	}

	@Test
	public void testUnifyWith() throws Exception {
		assertNull(stringType.unifyWith(new BoolTypeImpl()));
		assertEquals(nullType, stringType.unifyWith(nullType));
		assertEquals(stringType, stringType.unifyWith(primitiveType));
		assertEquals(stringType, stringType.unifyWith(new StringTypeImpl()));
	}
}
