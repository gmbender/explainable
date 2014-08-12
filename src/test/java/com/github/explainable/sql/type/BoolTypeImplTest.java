package com.github.explainable.sql.type;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link BoolTypeImpl}.
 */
public class BoolTypeImplTest {
	private BoolTypeImpl boolType = null;

	private PrimitiveBottomTypeImpl nullType = null;

	private PrimitiveTypeImpl primitiveType = null;

	@Before
	public void setUp() {
		boolType = new BoolTypeImpl();
		nullType = new PrimitiveBottomTypeImpl();
		primitiveType = new PrimitiveTypeImpl();
	}

	@After
	public void tearDown() {
		boolType = null;
		nullType = null;
		primitiveType = null;
	}

	@Test
	public void testIsSupertypeOf() throws Exception {
		assertTrue(boolType.isSupertypeOf(new BoolTypeImpl()));
		assertTrue(boolType.isSupertypeOf(nullType));
		assertFalse(boolType.isSupertypeOf(primitiveType));
		assertFalse(boolType.isSupertypeOf(new TableTypeImpl()));
	}

	@Test
	public void testEquals() throws Exception {
		// We avoid calling assertEquals() because we're not yet confident that our implementation
		// of equals(...) is symmetric, and it's undocumented whether assertEquals(X, Y) checks
		// X.equals(Y), Y.equals(X), or both.
		assertTrue(boolType.equals(new BoolTypeImpl()));
		assertFalse(boolType.equals(nullType));
		assertFalse(boolType.equals(primitiveType));
	}

	@Test
	public void testToBoolType() throws Exception {
		assertEquals(boolType, boolType.coerceToBool());
	}

	@Test
	public void testToNumericType() throws Exception {
		assertNull(boolType.coerceToNumeric());
	}

	@Test
	public void testToStringType() throws Exception {
		assertNull(boolType.coerceToString());
	}

	@Test
	public void testToPrimitiveType() throws Exception {
		assertEquals(boolType, boolType.coerceToPrimitive());
	}

	@Test
	public void testToTableType() throws Exception {
		assertNull(boolType.coerceToTable());
	}

	@Test
	public void testToSchemaTableType() throws Exception {
		assertNull(boolType.coerceToSchemaTable());
	}

	@Test
	public void testToSchemaListType() throws Exception {
		assertNull(boolType.coerceToSchemaList());
	}

	@Test
	public void testGetCommonSupertype() throws Exception {
		assertEquals(boolType, boolType.commonSupertype(new BoolTypeImpl()));
		assertEquals(boolType, boolType.commonSupertype(nullType));
		assertEquals(primitiveType, boolType.commonSupertype(primitiveType));
		assertEquals(primitiveType, boolType.commonSupertype(new StringTypeImpl()));
	}

	@Test
	public void testUnifyWith() throws Exception {
		assertEquals(boolType, boolType.unifyWith(new BoolTypeImpl()));
		assertEquals(nullType, boolType.unifyWith(nullType));
		assertEquals(boolType, boolType.unifyWith(primitiveType));
		assertNull(boolType.unifyWith(new StringTypeImpl()));
	}
}
