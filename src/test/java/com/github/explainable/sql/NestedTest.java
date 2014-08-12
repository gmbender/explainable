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

package com.github.explainable.sql;

import com.github.explainable.corelang.Term;
import com.github.explainable.corelang.View;
import com.github.explainable.sql.pipeline.Pipeline;
import com.github.explainable.sql.pipeline.passes.ViewExtractionPipeline;
import com.github.explainable.sql.table.TypedRelation;
import com.github.explainable.sql.table.TypedRelationImpl;
import com.google.common.collect.ImmutableList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static com.github.explainable.corelang.Atom.asMultisetAtom;
import static com.github.explainable.corelang.Atom.asSetAtom;
import static com.github.explainable.corelang.Terms.constant;
import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.multiset;
import static com.github.explainable.corelang.Terms.set;
import static com.github.explainable.corelang.View.asView;
import static com.github.explainable.sql.type.TypeSystem.numeric;
import static com.github.explainable.sql.type.TypeSystem.string;
import static com.github.explainable.util.MoreAsserts.assertEquivalentElements;

/**
 * End-to-end unit tests for the SQL parser and converter framework using the Sailors-and-Boats
 * examples from Section 5.4 of the Cow Book (Ramakrishnan and Gehrke. Database Management Systems,
 * 3rd Ed.), which focuses on nested queries.
 */
@SuppressWarnings("HardcodedLineSeparator")
public final class NestedTest {
	private static final TypedRelation SAILORS = TypedRelationImpl.builder().setName("Sailors")
			.addColumn("sid", numeric())
			.addColumn("sname", string())
			.addColumn("rating", numeric())
			.addColumn("age", numeric())
			.build();

	private static final TypedRelation BOATS = TypedRelationImpl.builder().setName("Boats")
			.addColumn("bid", numeric())
			.addColumn("bname", string())
			.addColumn("color", string())
			.build();

	private static final TypedRelation RESERVES = TypedRelationImpl.builder().setName("Reserves")
			.addColumn("sid", numeric())
			.addColumn("bid", numeric())
			.addColumn("day", string())
			.build();

	private static final Schema SCHEMA = Schema.of(SAILORS, BOATS, RESERVES);

	private CCJSqlParserManager parser = null;

	private Pipeline<ImmutableList<View>> extractor = null;

	@Before
	public void setUp() {
		parser = new CCJSqlParserManager();
		extractor = ViewExtractionPipeline.create(SCHEMA);
	}

	@After
	public void tearDown() {
		parser = null;
		extractor = null;
	}

	/**
	 * Section 5.4.1, Q1
	 */
	@Test
	public void testQ1() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S\n"
				+ "WHERE S.sid IN (SELECT R.sid\n"
				+ "                FROM Reserves R\n"
				+ "                WHERE R.bid = 103)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, dist(), multiset(), multiset()),
						asSetAtom(RESERVES, sid, constant(103L), set())),
				asView(asMultisetAtom(RESERVES, sid, constant(103L), multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set())));
	}

	/**
	 * Section 5.4.1, Q2
	 */
	@Test
	public void testQ2() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S\n"
				+ "WHERE S.sid IN (SELECT R.sid\n"
				+ "                FROM Reserves R\n"
				+ "                WHERE R.bid IN (SELECT B.bid\n"
				+ "                                FROM Boats B\n"
				+ "                                WHERE B.color = 'red'))\n";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		Term bid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, dist(), multiset(), multiset()),
						asSetAtom(RESERVES, sid, bid, set()),
						asSetAtom(BOATS, bid, set(), constant("red"))),
				asView(asMultisetAtom(RESERVES, sid, bid, multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(BOATS, bid, set(), constant("red"))),
				asView(asMultisetAtom(BOATS, bid, multiset(), constant("red")),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid, set())));
	}

	/**
	 * Section 5.4.1, Q21
	 */
	@Test
	public void testQ21() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S\n"
				+ "WHERE S.sid NOT IN (SELECT R.sid\n"
				+ "                    FROM Reserves R\n"
				+ "                    WHERE R.bid IN (SELECT B.bid\n"
				+ "                                    FROM Boats B\n"
				+ "                                    WHERE B.color = 'red'))";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		Term bid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, dist(), multiset(), multiset())),
				asView(asMultisetAtom(RESERVES, sid, bid, multiset()),
						asSetAtom(BOATS, bid, set(), constant("red")),
						asSetAtom(SAILORS, sid, set(), set(), set())),
				asView(asMultisetAtom(BOATS, bid, multiset(), constant("red")),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid, set())));
	}

	/**
	 * Section 5.4.2, variant of Q1 that uses EXISTS instead of IN
	 */
	@Test
	public void testQ1_exists() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S\n"
				+ "WHERE EXISTS (SELECT *\n"
				+ "              FROM Reserves R\n"
				+ "              WHERE R.bid = 103 AND R.sid = S.sid)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, dist(), multiset(), multiset()),
						asSetAtom(RESERVES, sid, constant(103L), set())),
				asView(asMultisetAtom(RESERVES, sid, constant(103L), multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set())));
	}

	/**
	 * Section 5.4.3, Q22
	 */
	@Test
	public void testQ22() throws Exception {
		String sql = "SELECT S.sid\n"
				+ "FROM Sailors S\n"
				+ "WHERE S.rating > ANY (SELECT S2.rating\n"
				+ "                      FROM Sailors S2\n"
				+ "                      WHERE S2.sname = 'Horatio')";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), dist(), multiset())),
				asView(asMultisetAtom(SAILORS, multiset(), constant("Horatio"), dist(), multiset()),
						asMultisetAtom(SAILORS, set(), set(), set(), set())));
	}

	/**
	 * Section 5.4.3, Q24
	 */
	@Test
	public void testQ24() throws Exception {
		String sql = "SELECT S.sid\n"
				+ "FROM Sailors S\n"
				+ "WHERE S.rating >= ALL (SELECT S2.rating FROM Sailors S2)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), dist(), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set())),
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), dist(), multiset())));
	}

	/**
	 * Section 5.4.4, Q6 (revisited)
	 */
	@Test
	public void testQ6() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S, Reserves R, Boats B\n"
				+ "WHERE S.sid = R.sid AND R.bid = B.bid AND B.color = 'red'\n"
				+ "      AND S.sid IN (SELECT S2.sid\n"
				+ "                    FROM Sailors S2, Reserves R2, Boats B2\n"
				+ "                    WHERE S2.sid = R2.sid AND R2.bid = B2.bid\n"
				+ "                          AND B2.color = 'green')";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		Term bid = dist();
		Term bid2 = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, dist(), multiset(), multiset()),
						asSetAtom(RESERVES, sid, bid, set()),
						asSetAtom(BOATS, bid, set(), constant("red")),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid2, set()),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(RESERVES, sid, bid, multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(BOATS, bid, set(), constant("red")),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid2, set()),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(BOATS, bid, multiset(), constant("red")),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid, set()),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid2, set()),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(SAILORS, sid, multiset(), multiset(), multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid, set()),
						asSetAtom(BOATS, bid, set(), constant("red")),
						asSetAtom(RESERVES, sid, bid2, set()),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(RESERVES, sid, bid2, multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid, set()),
						asSetAtom(BOATS, bid, set(), constant("red")),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(BOATS, bid2, multiset(), constant("green")),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid, set()),
						asSetAtom(BOATS, bid, set(), constant("red")),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid2, set())));
	}

	/**
	 * Section 5.4.4, Q9. JSqlParser apparently isn't able to handle {@code EXCEPT} inside a nested
	 * query.
	 */
	@Ignore("Currently not supported by CCJSqlParser")
	@Test
	public void testQ9() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S\n"
				+ "WHERE NOT EXISTS ((SELECT B.bid FROM Boats B)\n"
				+ "                  EXCEPT\n"
				+ "                  (SELECT R.bid FROM Reserves R WHERE R.sid = S.sid))";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), dist(), multiset(), multiset())),
				asView(asMultisetAtom(BOATS, dist(), multiset(), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set())),
				asView(asMultisetAtom(RESERVES, dist(), dist(), multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set())));
	}

	@Test
	public void testExists_ignoreSelectItems1() throws Exception {
		String sql = "SELECT 1 FROM Sailors WHERE EXISTS (SELECT * FROM Reserves)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, set(), set(), set())),
				asView(asMultisetAtom(RESERVES, multiset(), multiset(), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set())));
	}

	@Test
	public void testExists_ignoreSelectItems2() throws Exception {
		String sql = "SELECT 1 FROM Sailors WHERE EXISTS (SELECT sid, sname FROM Reserves)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, set(), set(), set())),
				asView(asMultisetAtom(RESERVES, multiset(), multiset(), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set())));
	}

	@Test
	public void testExists_ignoreSelectItems3() throws Exception {
		String sql = "SELECT 1 FROM Sailors S WHERE EXISTS\n"
				+ "(SELECT * FROM Reserves R WHERE S.sid = R.sid)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, sid, set(), set())),
				asView(asMultisetAtom(RESERVES, sid, multiset(), multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set())));
	}

	@Test
	public void testExists_ignoreSelectItemsRecursively1() throws Exception {
		String sql = "SELECT 1 FROM Sailors WHERE EXISTS\n"
				+ "            (SELECT * FROM Reserves WHERE EXISTS (SELECT * FROM Boats))";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, set(), set(), set()),
						asSetAtom(BOATS, set(), set(), set())),
				asView(asMultisetAtom(RESERVES, multiset(), multiset(), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set()),
						asSetAtom(BOATS, set(), set(), set())),
				asView(asMultisetAtom(BOATS, multiset(), multiset(), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set()),
						asSetAtom(RESERVES, set(), set(), set())));
	}

	@Test
	public void testExists_ignoreSelectItemsRecursively2() throws Exception {
		String sql = "SELECT 1 FROM Sailors WHERE EXISTS\n"
				+ "            (SELECT * FROM Reserves WHERE bid in (SELECT bid FROM Boats))";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term bid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, set(), bid, set()),
						asSetAtom(BOATS, bid, set(), set())),
				asView(asMultisetAtom(RESERVES, multiset(), bid, multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set()),
						asSetAtom(BOATS, bid, set(), set())),
				asView(asMultisetAtom(BOATS, bid, multiset(), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set()),
						asSetAtom(RESERVES, set(), bid, set())));
	}

	@Test
	public void testInList1() throws Exception {
		String sql = "SELECT 1 FROM Sailors S WHERE S.sid IN (4)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(4L), multiset(), multiset(), multiset())));
	}

	@Test
	public void testInList2() throws Exception {
		String sql = "SELECT 1 FROM Sailors S WHERE S.sid IN (4, 5)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())));
	}

	@Test
	public void testInList3() throws Exception {
		String sql = "SELECT 1 FROM Sailors S WHERE S.sid IN (4) OR S.sid IN (5)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())));
	}

	@Test
	public void testInSubSelect1() throws Exception {
		String sql = "SELECT 1 FROM Sailors WHERE sid IN (SELECT 4)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(4L), multiset(), multiset(), multiset())));
	}

	@Test
	public void testInSubSelect2() throws Exception {
		String sql = "SELECT 1 FROM Sailors WHERE sid IN (SELECT sid FROM Reserves WHERE sid = 4)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(4L), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, constant(4L), set(), set())),
				asView(asMultisetAtom(RESERVES, constant(4L), multiset(), multiset()),
						asSetAtom(SAILORS, constant(4L), set(), set(), set())));
	}

	@Test
	public void testInSubSelect3() throws Exception {
		String sql = "SELECT 1 FROM Sailors WHERE sid IN\n"
				+ "(SELECT SUM(sid) FROM Reserves WHERE sid = 4)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())),
				asView(asMultisetAtom(RESERVES, constant(4L), multiset(), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set())));
	}

	@Test
	public void testExistsSubSelect1() throws Exception {
		String sql = "SELECT 1 FROM Sailors S WHERE S.sid = 4 AND EXISTS\n"
				+ "(SELECT 1 FROM Reserves R WHERE R.sid = S.sid)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(4L), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, constant(4L), set(), set())),
				asView(asMultisetAtom(RESERVES, constant(4L), multiset(), multiset()),
						asSetAtom(SAILORS, constant(4L), set(), set(), set())));
	}

	@Test
	public void testExistsSubSelect2() throws Exception {
		String sql = "SELECT 1 FROM Sailors S WHERE S.sid = 4 AND EXISTS\n"
				+ "(SELECT COUNT(*) FROM Reserves R WHERE R.sid = S.sid)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(4L), multiset(), multiset(), multiset())),
				asView(asMultisetAtom(RESERVES, constant(4L), multiset(), multiset()),
						asSetAtom(SAILORS, constant(4L), set(), set(), set())));
	}

	@Test
	public void testExistsSubSelect3() throws Exception {
		String sql = "SELECT 1 FROM Sailors S WHERE EXISTS\n"
				+ "(SELECT 1 FROM Reserves R WHERE S.sid = 4 AND R.sid = S.sid)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(4L), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, constant(4L), set(), set())),
				asView(asMultisetAtom(RESERVES, constant(4L), multiset(), multiset()),
						asSetAtom(SAILORS, constant(4L), set(), set(), set())));
	}

	@Test
	public void testExistsSubSelect4() throws Exception {
		String sql = "SELECT 1 FROM Sailors S WHERE EXISTS\n"
				+ "(SELECT COUNT(*) FROM Reserves R WHERE S.sid = 4 AND R.sid = S.sid)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())),
				asView(asMultisetAtom(RESERVES, constant(4L), multiset(), multiset()),
						asSetAtom(SAILORS, constant(4L), set(), set(), set())));
	}

	@Test
	public void testExistsSubSelect_negated() throws Exception {
		String sql = "SELECT 1 FROM Sailors S WHERE NOT EXISTS\n"
				+ "(SELECT 1 FROM Reserves R WHERE S.sid = R.sid)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, multiset(), multiset(), multiset())),
				asView(asMultisetAtom(RESERVES, sid, multiset(), multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set())));
	}
}
