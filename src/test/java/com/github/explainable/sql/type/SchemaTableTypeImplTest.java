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
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link SchemaTableTypeImpl}.
 */
public class SchemaTableTypeImplTest {
	@Test
	public void testGetRowCount() throws Exception {
		assertEquals(RowCount.SINGLE_ROW,
				new SchemaTableTypeImpl(
						RowCount.SINGLE_ROW,
						ImmutableList.<PrimitiveType>of()).rowCount());

		assertEquals(RowCount.UNLIMITED_ROWS,
				new SchemaTableTypeImpl(
						RowCount.UNLIMITED_ROWS,
						ImmutableList.<PrimitiveType>of()).rowCount());
	}

	@Test
	public void testGetColumnTypes() throws Exception {
		ImmutableList<PrimitiveType> columns = ImmutableList.<PrimitiveType>of(
				new BoolTypeImpl(),
				new StringTypeImpl());

		assertEquals(columns,
				new SchemaTableTypeImpl(RowCount.UNLIMITED_ROWS, columns).columnTypes());
	}

	@Test
	public void testToSchemaListType_multiColumn() throws Exception {
		SchemaTableTypeImpl schemaTableType = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS,
				ImmutableList.<PrimitiveType>of(new BoolTypeImpl(), new StringTypeImpl()));

		assertNull(schemaTableType.coerceToSchemaList());
	}

	@Test
	public void testToSchemaListType_singleColumn() throws Exception {
		SchemaTableTypeImpl schemaTableType = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS,
				ImmutableList.<PrimitiveType>of(new BoolTypeImpl()));

		assertEquals(schemaTableType, schemaTableType.coerceToSchemaList());
	}

	@Test
	public void testIsSupertypeOf_simple() throws Exception {
		TableTypeImpl tableType = new TableTypeImpl();
		SchemaTableTypeImpl schemaTableType = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS,
				ImmutableList.<PrimitiveType>of(new BoolTypeImpl(), new StringTypeImpl()));

		assertFalse(schemaTableType.isSupertypeOf(tableType));
		assertTrue(schemaTableType.isSupertypeOf(schemaTableType));
	}

	@Test
	public void testIsSupertypeOf_rowCount() throws Exception {
		SchemaTableTypeImpl singleRow = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW, ImmutableList.<PrimitiveType>of());

		SchemaTableTypeImpl unlimitedRows = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		assertTrue(singleRow.isSupertypeOf(singleRow));
		assertFalse(singleRow.isSupertypeOf(unlimitedRows));
		assertTrue(unlimitedRows.isSupertypeOf(singleRow));
		assertTrue(unlimitedRows.isSupertypeOf(unlimitedRows));
	}

	@Test
	public void testIsSupertypeOf_differentArities() throws Exception {
		SchemaTableTypeImpl zeroColumnTableType = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		SchemaTableTypeImpl oneColumnTableType = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertTrue(zeroColumnTableType.isSupertypeOf(zeroColumnTableType));
		assertFalse(zeroColumnTableType.isSupertypeOf(oneColumnTableType));
		assertFalse(oneColumnTableType.isSupertypeOf(zeroColumnTableType));
		assertTrue(oneColumnTableType.isSupertypeOf(oneColumnTableType));
	}

	@Test
	public void testIsSupertypeOf_sameArity() throws Exception {
		SchemaTableTypeImpl boolTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new BoolTypeImpl()));

		SchemaTableTypeImpl primitiveTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertTrue(boolTable.isSupertypeOf(boolTable));
		assertFalse(boolTable.isSupertypeOf(primitiveTable));
		assertTrue(primitiveTable.isSupertypeOf(boolTable));
		assertTrue(primitiveTable.isSupertypeOf(primitiveTable));
	}

	@Test
	public void testEquals_types() throws Exception {
		// We avoid calling assertEquals() because we're not yet confident that our implementation
		// of equals(...) is symmetric, and it's undocumented whether assertEquals(X, Y) checks
		// X.equals(Y), Y.equals(X), or both.
		TableTypeImpl tableType = new TableTypeImpl();
		SchemaTableTypeImpl schemaTableType1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));
		SchemaTableTypeImpl schemaTableType2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertFalse(schemaTableType1.equals(tableType));
		assertTrue(schemaTableType1.equals(schemaTableType2));
	}

	@Test
	public void testEquals_rowCounts() throws Exception {
		SchemaTableTypeImpl singleRow1 = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW, ImmutableList.<PrimitiveType>of());
		SchemaTableTypeImpl singleRow2 = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW, ImmutableList.<PrimitiveType>of());
		SchemaTableTypeImpl unlimitedRows1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());
		SchemaTableTypeImpl unlimitedRows2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		assertTrue(singleRow1.equals(singleRow2));
		assertFalse(singleRow1.equals(unlimitedRows2));
		assertFalse(unlimitedRows1.equals(singleRow1));
		assertTrue(unlimitedRows1.equals(unlimitedRows2));
	}

	@Test
	public void testEquals_differentArities() throws Exception {
		SchemaTableTypeImpl zeroColumnTableType1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		SchemaTableTypeImpl zeroColumnTableType2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		SchemaTableTypeImpl oneColumnTableType1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		SchemaTableTypeImpl oneColumnTableType2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertTrue(zeroColumnTableType1.equals(zeroColumnTableType2));
		assertFalse(zeroColumnTableType1.equals(oneColumnTableType2));
		assertFalse(oneColumnTableType1.equals(zeroColumnTableType2));
		assertTrue(oneColumnTableType1.equals(oneColumnTableType2));
	}

	@Test
	public void testEquals_sameArity() throws Exception {
		SchemaTableTypeImpl boolTable1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new BoolTypeImpl()));

		SchemaTableTypeImpl boolTable2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new BoolTypeImpl()));

		SchemaTableTypeImpl primitiveTable1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		SchemaTableTypeImpl primitiveTable2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertTrue(boolTable1.equals(boolTable2));
		assertFalse(boolTable1.equals(primitiveTable2));
		assertFalse(primitiveTable1.equals(boolTable2));
		assertTrue(primitiveTable1.equals(primitiveTable2));
	}

	@Test
	public void testGetArity() throws Exception {
		SchemaTableTypeImpl zeroColumnTableType = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		SchemaTableTypeImpl oneColumnTableType = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertEquals(0, zeroColumnTableType.arity());

		assertEquals(1, oneColumnTableType.arity());
	}

	@Test
	public void testGetCommonSupertype_types() throws Exception {
		TableTypeImpl tableType = new TableTypeImpl();
		SchemaTableTypeImpl schemaTableType1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));
		SchemaTableTypeImpl schemaTableType2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertEquals(tableType, schemaTableType1.commonSupertype(tableType));
		assertEquals(schemaTableType1, schemaTableType1.commonSupertype(schemaTableType2));
	}

	@Test
	public void testGetCommonSupertype_rowCounts() throws Exception {
		SchemaTableTypeImpl singleRow1 = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW, ImmutableList.<PrimitiveType>of());
		SchemaTableTypeImpl singleRow2 = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW, ImmutableList.<PrimitiveType>of());
		SchemaTableTypeImpl unlimitedRows1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());
		SchemaTableTypeImpl unlimitedRows2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		assertEquals(singleRow1, singleRow1.commonSupertype(singleRow2));
		assertEquals(unlimitedRows1, singleRow1.commonSupertype(unlimitedRows2));
		assertEquals(unlimitedRows1, unlimitedRows1.commonSupertype(singleRow1));
		assertEquals(unlimitedRows1, unlimitedRows1.commonSupertype(unlimitedRows2));
	}

	@Test
	public void testGetCommonSupertype_differentArities() throws Exception {
		SchemaTableTypeImpl zeroColumnTableType1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		SchemaTableTypeImpl zeroColumnTableType2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		SchemaTableTypeImpl oneColumnTableType1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		SchemaTableTypeImpl oneColumnTableType2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertEquals(zeroColumnTableType1,
				zeroColumnTableType1.commonSupertype(zeroColumnTableType2));
		assertEquals(new TableTypeImpl(),
				zeroColumnTableType1.commonSupertype(oneColumnTableType2));
		assertEquals(new TableTypeImpl(),
				oneColumnTableType1.commonSupertype(zeroColumnTableType2));
		assertEquals(oneColumnTableType1,
				oneColumnTableType1.commonSupertype(oneColumnTableType2));
	}

	@Test
	public void testGetCommonSupertype_sameArity() throws Exception {
		SchemaTableTypeImpl boolTable1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new BoolTypeImpl()));

		SchemaTableTypeImpl boolTable2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new BoolTypeImpl()));

		SchemaTableTypeImpl primitiveTable1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		SchemaTableTypeImpl primitiveTable2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertEquals(boolTable1, boolTable1.commonSupertype(boolTable2));
		assertEquals(primitiveTable1, boolTable1.commonSupertype(primitiveTable2));
		assertEquals(primitiveTable1, primitiveTable1.commonSupertype(boolTable2));
		assertEquals(primitiveTable1, primitiveTable1.commonSupertype(primitiveTable2));
	}

	@Test
	public void testUnifyWith_types() throws Exception {
		TableTypeImpl tableType = new TableTypeImpl();
		SchemaTableTypeImpl schemaTableType1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));
		SchemaTableTypeImpl schemaTableType2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertEquals(schemaTableType1, tableType.unifyWith(schemaTableType2));
		assertEquals(schemaTableType1, schemaTableType1.unifyWith(schemaTableType2));
	}

	@Test
	public void testUnifyWith_rowCounts() throws Exception {
		SchemaTableTypeImpl singleRow1 = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW, ImmutableList.<PrimitiveType>of());
		SchemaTableTypeImpl singleRow2 = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW, ImmutableList.<PrimitiveType>of());
		SchemaTableTypeImpl unlimitedRows1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());
		SchemaTableTypeImpl unlimitedRows2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		assertEquals(singleRow1, singleRow1.unifyWith(singleRow2));
		assertEquals(singleRow1, singleRow1.unifyWith(unlimitedRows2));
		assertEquals(singleRow1, unlimitedRows1.unifyWith(singleRow1));
		assertEquals(unlimitedRows1, unlimitedRows1.unifyWith(unlimitedRows2));
	}

	@Test
	public void testUnifyWith_differentArities() throws Exception {
		SchemaTableTypeImpl zeroColumnTableType1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		SchemaTableTypeImpl zeroColumnTableType2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		SchemaTableTypeImpl oneColumnTableType1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		SchemaTableTypeImpl oneColumnTableType2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertEquals(zeroColumnTableType1,
				zeroColumnTableType1.unifyWith(zeroColumnTableType2));
		assertNull(zeroColumnTableType1.unifyWith(oneColumnTableType2));
		assertNull(oneColumnTableType1.unifyWith(zeroColumnTableType2));
		assertEquals(oneColumnTableType1,
				oneColumnTableType1.unifyWith(oneColumnTableType2));
	}

	@Test
	public void testUnifyWith_sameArity() throws Exception {
		SchemaTableTypeImpl boolTable1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new BoolTypeImpl()));

		SchemaTableTypeImpl boolTable2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new BoolTypeImpl()));

		SchemaTableTypeImpl primitiveTable1 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		SchemaTableTypeImpl primitiveTable2 = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertEquals(boolTable1, boolTable1.unifyWith(boolTable2));
		assertEquals(boolTable1, boolTable1.unifyWith(primitiveTable2));
		assertEquals(boolTable1, primitiveTable1.unifyWith(boolTable2));
		assertEquals(primitiveTable1, primitiveTable1.unifyWith(primitiveTable2));
	}

	@Test
	public void testUnifyWith() throws Exception {
		// TODO
	}

	@Test
	public void testToBoolType() throws Exception {
		SchemaTableType zeroColumnTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		SchemaTableTypeImpl primitiveTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		SchemaTableTypeImpl singleRowNullTable = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW,
				ImmutableList.<PrimitiveType>of(new PrimitiveBottomTypeImpl()));

		SchemaTableTypeImpl singleRowBoolTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new BoolTypeImpl()));

		SchemaTableTypeImpl unlimitedRowBoolTable = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW, ImmutableList.<PrimitiveType>of(new BoolTypeImpl()));

		SchemaTableTypeImpl stringTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new StringTypeImpl()));

		assertNull(zeroColumnTable.coerceToBool());
		assertNull(primitiveTable.coerceToBool());
		assertEquals(new PrimitiveBottomTypeImpl(), singleRowNullTable.coerceToBool());
		assertNull(singleRowBoolTable.coerceToBool());
		assertEquals(new BoolTypeImpl(), unlimitedRowBoolTable.coerceToBool());
		assertNull(stringTable.coerceToBool());
	}

	@Test
	public void testToNumericType() throws Exception {
		SchemaTableType zeroColumnTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		SchemaTableTypeImpl primitiveTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		SchemaTableTypeImpl singleRowNullTable = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW,
				ImmutableList.<PrimitiveType>of(new PrimitiveBottomTypeImpl()));

		SchemaTableTypeImpl singleRowNumericTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new NumericTypeImpl()));

		SchemaTableTypeImpl unlimitedRowNumericTable = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW, ImmutableList.<PrimitiveType>of(new NumericTypeImpl()));

		SchemaTableTypeImpl stringTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new StringTypeImpl()));

		assertNull(zeroColumnTable.coerceToNumeric());
		assertNull(primitiveTable.coerceToNumeric());
		assertEquals(new PrimitiveBottomTypeImpl(), singleRowNullTable.coerceToNumeric());
		assertNull(singleRowNumericTable.coerceToNumeric());
		assertEquals(new NumericTypeImpl(), unlimitedRowNumericTable.coerceToNumeric());
		assertNull(stringTable.coerceToNumeric());
	}

	@Test
	public void testToStringType() throws Exception {
		SchemaTableType zeroColumnTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		SchemaTableTypeImpl primitiveTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		SchemaTableTypeImpl singleRowNullTable = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW,
				ImmutableList.<PrimitiveType>of(new PrimitiveBottomTypeImpl()));

		SchemaTableTypeImpl unlimitedStringTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new StringTypeImpl()));

		SchemaTableTypeImpl singleStringTable = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW, ImmutableList.<PrimitiveType>of(new StringTypeImpl()));

		SchemaTableTypeImpl boolTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new BoolTypeImpl()));

		assertNull(zeroColumnTable.coerceToString());
		assertNull(primitiveTable.coerceToString());
		assertEquals(new PrimitiveBottomTypeImpl(), singleRowNullTable.coerceToString());
		assertNull(boolTable.coerceToString());
		assertNull(unlimitedStringTable.coerceToString());
		assertEquals(new StringTypeImpl(), singleStringTable.coerceToString());
	}

	@Test
	public void testToPrimitiveType() throws Exception {
		SchemaTableType zeroColumnTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of());

		SchemaTableTypeImpl primitiveTable = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		SchemaTableTypeImpl singleRowNullTable = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW,
				ImmutableList.<PrimitiveType>of(new PrimitiveBottomTypeImpl()));

		SchemaTableTypeImpl unlimitedPrimitive = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		SchemaTableTypeImpl singletonPrimitive = new SchemaTableTypeImpl(
				RowCount.SINGLE_ROW, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertNull(zeroColumnTable.coerceToPrimitive());
		assertNull(primitiveTable.coerceToPrimitive());
		assertEquals(new PrimitiveBottomTypeImpl(), singleRowNullTable.coerceToPrimitive());
		assertNull(unlimitedPrimitive.coerceToPrimitive());
		assertEquals(new PrimitiveTypeImpl(), singletonPrimitive.coerceToPrimitive());
	}

	@Test
	public void testToTableType() throws Exception {
		SchemaTableTypeImpl table = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertEquals(table, table.coerceToTable());
	}

	@Test
	public void testToSchemaTableType() throws Exception {
		SchemaTableTypeImpl table = new SchemaTableTypeImpl(
				RowCount.UNLIMITED_ROWS, ImmutableList.<PrimitiveType>of(new PrimitiveTypeImpl()));

		assertEquals(table, table.coerceToSchemaTable());
	}
}
