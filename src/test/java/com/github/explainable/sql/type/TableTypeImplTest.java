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

import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link TableTypeImpl}.
 */
public class TableTypeImplTest {
	private TableTypeImpl tableType;

	private SchemaTableTypeImpl schemaTableType;

	@Before
	public void setUp() {
		tableType = new TableTypeImpl();
		schemaTableType = new SchemaTableTypeImpl(RowCount.UNLIMITED_ROWS,
				ImmutableList.<PrimitiveType>of());
	}

	@After
	public void tearDown() {
		tableType = null;
		schemaTableType = null;
	}

	@Test
	public void testIsSupertypeOf() throws Exception {
		assertTrue(tableType.isSupertypeOf(new TableTypeImpl()));
		assertTrue(tableType.isSupertypeOf(schemaTableType));
	}

	@Test
	public void testEquals() throws Exception {
		// We avoid calling assertEquals() because we're not yet confident that our implementation
		// of equals(...) is symmetric, and it's undocumented whether assertEquals(X, Y) checks
		// X.equals(Y), Y.equals(X), or both.
		assertTrue(tableType.equals(new TableTypeImpl()));
		assertFalse(tableType.equals(schemaTableType));
	}

	@Test
	public void testToBoolType() throws Exception {
		assertNull(tableType.coerceToBool());
	}

	@Test
	public void testToNumericType() throws Exception {
		assertNull(tableType.coerceToNumeric());
	}

	@Test
	public void testToStringType() throws Exception {
		assertNull(tableType.coerceToString());
	}

	@Test
	public void testToPrimitiveType() throws Exception {
		assertNull(tableType.coerceToPrimitive());
	}

	@Test
	public void testToTableType() throws Exception {
		assertEquals(tableType, tableType.coerceToTable());
	}

	@Test
	public void testToSchemaTableType() throws Exception {
		assertNull(tableType.coerceToSchemaTable());
	}

	@Test
	public void testToSchemaListType() throws Exception {
		assertNull(tableType.coerceToSchemaList());
	}

	@Test
	public void testGetCommonSupertype() throws Exception {
		assertEquals(tableType, tableType.commonSupertype(new TableTypeImpl()));
		assertEquals(tableType, tableType.commonSupertype(schemaTableType));
	}

	@Test
	public void testUnifyWith() throws Exception {
		assertEquals(tableType, tableType.unifyWith(new TableTypeImpl()));
		assertEquals(schemaTableType, tableType.unifyWith(schemaTableType));
	}
}
