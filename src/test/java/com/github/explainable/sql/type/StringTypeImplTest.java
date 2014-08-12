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
