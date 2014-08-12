package com.github.explainable.sql.table;

import com.github.explainable.sql.type.TypeSystem;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link TypedRelationParser}.
 */
public class TypedRelationParserTest {
	@Test
	public void testEmpty1() {
		assertEquals(
				TypedRelationImpl.builder().setName("foo").build(),
				TypedRelationParser.create("CREATE TABLE foo();").parse());
	}

	@Test
	public void testEmpty2() {
		assertEquals(
				TypedRelationImpl.builder().setName("barBazBak").build(),
				TypedRelationParser.create("CREATE TABLE barBazBak();").parse());
	}

	@Test
	public void testCaseInsensitive() {
		assertEquals(
				TypedRelationImpl.builder().setName("foo").build(),
				TypedRelationParser.create("CrEaTe TaBlE foo();").parse());
	}

	@Test
	public void testType_primitive() {
		assertEquals(
				TypedRelationImpl.builder().setName("foo")
						.addColumn("a", TypeSystem.primitive())
						.build(),
				TypedRelationParser.create("CREATE TABLE foo(a PRIMITIVE);").parse());
	}

	@Test
	public void testType_string() {
		assertEquals(
				TypedRelationImpl.builder().setName("foo")
						.addColumn("a", TypeSystem.string())
						.build(),
				TypedRelationParser.create("CREATE TABLE foo(a STRING);").parse());
	}

	@Test
	public void testType_numeric() {
		assertEquals(
				TypedRelationImpl.builder().setName("foo")
						.addColumn("a", TypeSystem.numeric())
						.build(),
				TypedRelationParser.create("CREATE TABLE foo(a NUMERIC);").parse());
	}

	@Test
	public void testType_bool() {
		assertEquals(
				TypedRelationImpl.builder().setName("foo")
						.addColumn("a", TypeSystem.bool())
						.build(),
				TypedRelationParser.create("CREATE TABLE foo(a BOOL);").parse());
	}

	@Test
	public void testColumn_longerName() {
		assertEquals(
				TypedRelationImpl.builder().setName("foo")
						.addColumn("peter_Piper4", TypeSystem.bool())
						.build(),
				TypedRelationParser.create("CREATE TABLE foo(peter_Piper4 BOOL);").parse());
	}

	@Test
	public void testType_list() {
		assertEquals(
				TypedRelationImpl.builder().setName("foo")
						.addColumn("a", TypeSystem.numeric())
						.addColumn("b", TypeSystem.bool())
						.addColumn("c", TypeSystem.primitive())
						.addColumn("d", TypeSystem.string())
						.build(),
				TypedRelationParser.create(
						"CREATE TABLE foo(a NUMERIC, b BOOL, c PRIMITIVE, d STRING);")
						.parse());
	}

	@Test
	public void testSpaceInsensitivity1() {
		assertEquals(
				TypedRelationImpl.builder().setName("foo").build(),
				TypedRelationParser.create("     CREATE\tTABLE\nfoo(\n\t\t)      ; ").parse());
	}

	@Test
	public void testSpaceInsensitivity2() {
		assertEquals(
				TypedRelationImpl.builder().setName("foo")
						.addColumn("a", TypeSystem.numeric())
						.build(),
				TypedRelationParser.create("CREATE TABLE  foo(    a \t NUMERIC        )      ; ")
						.parse());
	}
}