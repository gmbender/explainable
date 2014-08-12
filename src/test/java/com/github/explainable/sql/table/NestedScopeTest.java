package com.github.explainable.sql.table;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.ast.expression.SqlNumericConstant;
import com.github.explainable.sql.ast.select.SqlPlainSelect;
import com.github.explainable.sql.ast.select.SqlSelectColumn;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.github.explainable.sql.type.TypeSystem.primitive;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 10/22/13 Time: 12:40 PM To change this template
 * use File | Settings | File Templates.
 */
public class NestedScopeTest {
	private NestedScope outerNestedScope = null;

	private NestedScope innerNestedScope = null;

	private BaseTable outerTable = null;

	private BaseTable innerTable = null;

	private SqlPlainSelect select = null;

	@Before
	public void setUp() {
		TypedRelation rel = TypedRelationImpl.builder().setName("Rel")
				.addColumn("X", primitive())
				.addColumn("Y", primitive())
				.build();

		select = SqlPlainSelect.builder()
				.addSelectItem(new SqlSelectColumn(new SqlNumericConstant(1L), "1"))
				.build();

		outerNestedScope = NestedScope.create(select, null);
		outerTable = outerNestedScope.createBaseTable(rel, "Outer");

		innerNestedScope = NestedScope.create(select, outerNestedScope);
		innerTable = innerNestedScope.createBaseTable(rel, "Inner");
	}

	@After
	public void tearDown() {
		innerTable = null;
		outerTable = null;
		innerNestedScope = null;
		outerNestedScope = null;
		select = null;
	}

	@Test
	public void testFindColumn_outer() throws Exception {
		assertEquals(outerTable.findColumn("X"), outerNestedScope.findColumn("Outer", "X"));
		assertEquals(outerTable.findColumn("Y"), outerNestedScope.findColumn("Outer", "Y"));
	}

	@Test(expected = SqlException.class)
	public void testFindColumn_outerFail() throws Exception {
		outerNestedScope.findColumn("Inner", "X");
	}

	@Test
	public void testFindColumn_inner() throws Exception {
		assertEquals(outerTable.findColumn("X"), innerNestedScope.findColumn("Outer", "X"));
		assertEquals(outerTable.findColumn("Y"), innerNestedScope.findColumn("Outer", "Y"));
		assertEquals(outerTable.findColumn("X"), innerNestedScope.findColumn("Outer", "X"));
		assertEquals(outerTable.findColumn("Y"), innerNestedScope.findColumn("Outer", "Y"));
	}

	@Test
	public void testColumnsInTable_outer() throws Exception {
		assertEquals(outerTable.columns(), outerNestedScope.columnsInTable("Outer"));
	}

	@Test(expected = SqlException.class)
	public void testColumnsInTable_outerFail() throws Exception {
		outerNestedScope.columnsInTable("Inner");
	}

	@Test
	public void testColumnsInTable_inner() throws Exception {
		assertEquals(outerTable.columns(), innerNestedScope.columnsInTable("Outer"));
		assertEquals(innerTable.columns(), innerNestedScope.columnsInTable("Inner"));
	}

	@Test
	public void testColumnsInCurrentScope() throws Exception {
		assertEquals(outerTable.columns(), outerNestedScope.localColumns());
		assertEquals(innerTable.columns(), innerNestedScope.localColumns());
	}

	private static final TypedRelation RELATION = TypedRelationImpl.builder().setName("R")
			.addColumn("a", primitive())
			.addColumn("b", primitive())
			.addColumn("c", primitive())
			.build();

	@Test
	public void testCreateBaseTable_smokeTest() {
		NestedScope scope = NestedScope.create(select, null);
		scope.createBaseTable(RELATION, "R");
	}

	@Test
	public void testCreateBaseTable_twoDifferentAliases() {
		NestedScope scope = NestedScope.create(select, null);

		scope.createBaseTable(RELATION, "R1");
		scope.createBaseTable(RELATION, "R2");
	}

	@Test(expected = SqlException.class)
	public void testCreateBaseTable_aliasConflict() {
		NestedScope scope = NestedScope.create(select, null);
		scope.createBaseTable(RELATION, "R");
		scope.createBaseTable(RELATION, "R");
	}

	@Test(expected = NullPointerException.class)
	public void testCreateBaseTable_relationIsNull() {
		NestedScope scope = NestedScope.create(select, null);
		scope.createBaseTable(null, "A");
	}

	@Test(expected = NullPointerException.class)
	public void testCreateBaseTable_aliasIsNull() {
		NestedScope scope = NestedScope.create(select, null);
		scope.createBaseTable(RELATION, null);
	}

	@Test(expected = SqlException.class)
	public void testGetColumn_notFound() {
		NestedScope scope = NestedScope.create(select, null);
		scope.findColumn(null, "a");
	}

	@Test
	public void testGetColumn_columnLookup() {
		NestedScope scope = NestedScope.create(select, null);
		Table rBuilder = scope.createBaseTable(RELATION, "R");

		assertEquals(rBuilder.columns().get(0), scope.findColumn(null, "a"));
		assertEquals(rBuilder.columns().get(1), scope.findColumn(null, "b"));
		assertEquals(rBuilder.columns().get(2), scope.findColumn(null, "c"));
	}

	@Test
	public void testGetColumn_columnLookupCalledTwice() {
		NestedScope scope = NestedScope.create(select, null);
		scope.createBaseTable(RELATION, "R");

		assertEquals(scope.findColumn(null, "a"), scope.findColumn(null, "a"));
	}

	@Test(expected = NullPointerException.class)
	public void testGetColumn_columnNameIsNull() {
		NestedScope scope = NestedScope.create(select, null);
		scope.findColumn(null, null);
	}

	@Test(expected = SqlException.class)
	public void testGetColumn_columnNameIsAmbiguous() {
		NestedScope scope = NestedScope.create(select, null);
		scope.createBaseTable(RELATION, "R1");
		scope.createBaseTable(RELATION, "R2");
		scope.findColumn(null, "a");
	}

	@Test
	public void testGetColumn_fullyQualifiedName() {
		NestedScope scope = NestedScope.create(select, null);
		Table rBuilder = scope.createBaseTable(RELATION, "AliasOfR");

		assertEquals(rBuilder.columns().get(0), scope.findColumn("AliasOfR", "a"));
		assertEquals(rBuilder.columns().get(1), scope.findColumn("AliasOfR", "b"));
		assertEquals(rBuilder.columns().get(2), scope.findColumn("AliasOfR", "c"));
	}

	@Test
	public void testGetColumn_fullyQualifiedNameWithDuplicateRelation() {
		NestedScope scope = NestedScope.create(select, null);
		Table r1Builder = scope.createBaseTable(RELATION, "R1");
		Table r2Builder = scope.createBaseTable(RELATION, "R2");

		assertEquals(r1Builder.columns().get(0), scope.findColumn("R1", "a"));
		assertEquals(r1Builder.columns().get(1), scope.findColumn("R1", "b"));
		assertEquals(r1Builder.columns().get(2), scope.findColumn("R1", "c"));

		assertEquals(r2Builder.columns().get(0), scope.findColumn("R2", "a"));
		assertEquals(r2Builder.columns().get(1), scope.findColumn("R2", "b"));
		assertEquals(r2Builder.columns().get(2), scope.findColumn("R2", "c"));
	}

	@Test(expected = SqlException.class)
	public void testGetColumn_fullyQualifiedNameNotFound1() {
		NestedScope scope = NestedScope.create(select, null);
		scope.createBaseTable(RELATION, "AliasOfR");
		scope.findColumn("R", "a");
	}

	@Test(expected = SqlException.class)
	public void testGetColumn_fullyQualifiedNameNotFound2() {
		NestedScope scope = NestedScope.create(select, null);
		scope.createBaseTable(RELATION, "AliasOfR");
		scope.findColumn("R", "nonexistent_column");
	}
}
