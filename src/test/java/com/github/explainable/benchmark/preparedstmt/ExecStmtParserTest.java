package com.github.explainable.benchmark.preparedstmt;

import com.github.explainable.corelang.Term;
import com.github.explainable.corelang.Terms;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ExecStmtParser}.
 */
public class ExecStmtParserTest {
	@Test
	public void testEmpty1() {
		assertEquals(
				SqlExec.create("foo", ImmutableList.<Term>of()),
				ExecStmtParser.create("EXECUTE foo();").parse());
	}

	@Test
	public void testEmpty2() {
		assertEquals(
				SqlExec.create("FooBar34", ImmutableList.<Term>of()),
				ExecStmtParser.create("EXECUTE FooBar34();").parse());
	}

	@Test
	public void testEmpty3() {
		assertEquals(
				SqlExec.create("foo", ImmutableList.<Term>of()),
				ExecStmtParser.create("EXECUTE foo();").parse());
	}

	@Test
	public void testSingleString() {
		assertEquals(
				SqlExec.create("foo", ImmutableList.of(Terms.constant("baz"))),
				ExecStmtParser.create("EXECUTE foo('baz');").parse());
	}

	@Test
	public void testSingleMultiwordString() {
		assertEquals(
				SqlExec.create("ft", ImmutableList.of(Terms.constant("Meaning of life?"))),
				ExecStmtParser.create("EXECUTE ft('Meaning of life?');").parse());
	}

	@Test
	public void testSingleNumber() {
		assertEquals(
				SqlExec.create("foo", ImmutableList.of(Terms.constant(42L))),
				ExecStmtParser.create("EXECUTE foo(42);").parse());
	}

	@Test
	public void testArgumentList() {
		assertEquals(
				SqlExec.create("adams", ImmutableList.of(
						Terms.constant("The question of life"),
						Terms.constant("is"),
						Terms.constant(6L),
						Terms.constant("multiplied by"),
						Terms.constant(9L))),
				ExecStmtParser.create(
						"EXECUTE adams('The question of life', 'is', 6, 'multiplied by', 9);")
						.parse()
		);
	}
}
