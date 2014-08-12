package com.github.explainable.sql.type;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/23/13 Time: 3:06 PM To change this template use
 * File | Settings | File Templates.
 */
public class PrimitiveTypeImplTest {
	private BoolTypeImpl boolType = null;

	private PrimitiveBottomTypeImpl nullType = null;

	private NumericTypeImpl numericType = null;

	private PrimitiveTypeImpl primitiveType = null;

	private StringTypeImpl stringType = null;

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
		assertTrue(primitiveType.isSupertypeOf(boolType));
		assertTrue(primitiveType.isSupertypeOf(nullType));
		assertTrue(primitiveType.isSupertypeOf(numericType));
		assertTrue(primitiveType.isSupertypeOf(new PrimitiveTypeImpl()));
		assertTrue(primitiveType.isSupertypeOf(stringType));
	}

	@Test
	public void testEquals() throws Exception {
		// We avoid calling assertEquals() because we're not yet confident that our implementation
		// of equals(...) is symmetric, and it's undocumented whether assertEquals(X, Y) checks
		// X.equals(Y), Y.equals(X), or both.
		assertTrue(primitiveType.equals(new PrimitiveTypeImpl()));
		assertFalse(primitiveType.equals(new StringTypeImpl()));
	}

	@Test
	public void testToBoolType() throws Exception {
		assertNull(primitiveType.coerceToBool());
	}

	@Test
	public void testToNumericType() throws Exception {
		assertNull(primitiveType.coerceToNumeric());
	}

	@Test
	public void testToStringType() throws Exception {
		assertNull(primitiveType.coerceToString());
	}

	@Test
	public void testToPrimitiveType() throws Exception {
		assertEquals(primitiveType, primitiveType.coerceToPrimitive());
	}

	@Test
	public void testToTableType() throws Exception {
		assertNull(primitiveType.coerceToTable());
	}

	@Test
	public void testToSchemaTableType() throws Exception {
		assertNull(primitiveType.coerceToSchemaTable());
	}

	@Test
	public void testToSchemaListType() throws Exception {
		assertNull(primitiveType.coerceToSchemaList());
	}

	@Test
	public void testGetCommonSupertype() throws Exception {
		assertEquals(primitiveType, primitiveType.commonSupertype(boolType));
		assertEquals(primitiveType, primitiveType.commonSupertype(nullType));
		assertEquals(primitiveType, primitiveType.commonSupertype(numericType));
		assertEquals(primitiveType, primitiveType.commonSupertype(primitiveType));
		assertEquals(primitiveType, primitiveType.commonSupertype(stringType));
	}

	@Test
	public void testUnifyWith() throws Exception {
		assertEquals(boolType, primitiveType.unifyWith(boolType));
		assertEquals(nullType, primitiveType.unifyWith(nullType));
		assertEquals(numericType, primitiveType.unifyWith(numericType));
		assertEquals(primitiveType, primitiveType.unifyWith(primitiveType));
		assertEquals(stringType, primitiveType.unifyWith(stringType));
	}
}
