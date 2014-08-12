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
 * End-to-end tests for atom extraction on queries involving outer joins.
 */
@SuppressWarnings("HardcodedLineSeparator")
public class OuterJoinTest {
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
	 * Unnamed query in Section 5.6.4 of the Cow Book. JSqlParser apparently isn't able to handle
	 * {@code NATURAL LEFT OUTER JOIN}.
	 */
	@Ignore("Currently not supported by CCJSqlParser")
	@Test
	public void testNaturalLeftOuterJoin() throws Exception {
		String sql = "SELECT S.sid, R.bid FROM Sailors S NATURAL LEFT OUTER JOIN Reserves R";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, sid, set(), set())),
				asView(asMultisetAtom(RESERVES, sid, dist(), multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set())));
	}

	@Test
	public void testLeftOuterJoinEquality_where_left() throws Exception {
		String sql = "SELECT 1 FROM Sailors S LEFT OUTER JOIN Reserves R\n"
				+ "WHERE S.sid = R.sid AND S.sid = 42";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(42L), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, constant(42L), set(), set())),
				asView(asMultisetAtom(RESERVES, constant(42L), multiset(), multiset()),
						asSetAtom(SAILORS, constant(42L), set(), set(), set())));
	}

	@Test
	public void testLeftOuterJoinEquality_where_right() throws Exception {
		String sql = "SELECT 1 FROM Sailors S LEFT OUTER JOIN Reserves R\n"
				+ "WHERE S.sid = R.sid AND R.sid = 42";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(42L), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, constant(42L), set(), set())),
				asView(asMultisetAtom(RESERVES, constant(42L), multiset(), multiset()),
						asSetAtom(SAILORS, constant(42L), set(), set(), set())));
	}

	@Test
	public void testLeftOuterJoinEquality_on_left() throws Exception {
		String sql = "SELECT 1 FROM Sailors S LEFT OUTER JOIN Reserves R ON (S.sid = R.sid)\n"
				+ "WHERE S.sid = 42";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(42L), multiset(), multiset(), multiset())),
				asView(asMultisetAtom(RESERVES, constant(42L), multiset(), multiset()),
						asSetAtom(SAILORS, constant(42L), set(), set(), set())));
	}

	@Test
	public void testLeftOuterJoinEquality_on_right() throws Exception {
		String sql = "SELECT 1 FROM Sailors S LEFT OUTER JOIN Reserves R\n"
				+ "ON (S.sid = R.sid AND R.sid = 42)";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())),
				asView(asMultisetAtom(RESERVES, constant(42L), multiset(), multiset()),
						asSetAtom(SAILORS, constant(42L), set(), set(), set())));
	}

	@Test
	public void testRightOuterJoinEquality_where_left() throws Exception {
		String sql = "SELECT 1 FROM Sailors S RIGHT OUTER JOIN Reserves R\n"
				+ "WHERE S.sid = R.sid AND S.sid = 42";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(42L), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, constant(42L), set(), set())),
				asView(asMultisetAtom(RESERVES, constant(42L), multiset(), multiset()),
						asSetAtom(SAILORS, constant(42L), set(), set(), set())));
	}

	@Test
	public void testRightOuterJoinEquality_where_right() throws Exception {
		String sql = "SELECT 1 FROM Sailors S RIGHT OUTER JOIN Reserves R\n"
				+ "WHERE S.sid = R.sid AND R.sid = 42";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(42L), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, constant(42L), set(), set())),
				asView(asMultisetAtom(RESERVES, constant(42L), multiset(), multiset()),
						asSetAtom(SAILORS, constant(42L), set(), set(), set())));
	}

	@Test
	public void testRightOuterJoinEquality_on_left() throws Exception {
		String sql = "SELECT 1 FROM Sailors S RIGHT OUTER JOIN Reserves R\n"
				+ "ON (S.sid = R.sid AND S.sid = 42)";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(42L), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, constant(42L), set(), set())),
				asView(asMultisetAtom(RESERVES, dist(), multiset(), multiset())));
	}

	@Test
	public void testRightOuterJoinEquality_on_right() throws Exception {
		String sql = "SELECT 1 FROM Sailors S RIGHT OUTER JOIN Reserves R ON (S.sid = R.sid)\n"
				+ "WHERE R.sid = 42";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(42L), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, constant(42L), set(), set())),
				asView(asMultisetAtom(RESERVES, constant(42L), multiset(), multiset())));
	}

	@Test
	public void testFullOuterJoinEquality_where_left() throws Exception {
		String sql = "SELECT 1 FROM Sailors S FULL OUTER JOIN Reserves R\n"
				+ "WHERE S.sid = R.sid AND S.sid = 42";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(42L), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, constant(42L), set(), set())),
				asView(asMultisetAtom(RESERVES, constant(42L), multiset(), multiset()),
						asSetAtom(SAILORS, constant(42L), set(), set(), set())));
	}

	@Test
	public void testFullOuterJoinEquality_where_right() throws Exception {
		String sql = "SELECT 1 FROM Sailors S FULL OUTER JOIN Reserves R\n"
				+ "WHERE S.sid = R.sid AND R.sid = 42";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(42L), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, constant(42L), set(), set())),
				asView(asMultisetAtom(RESERVES, constant(42L), multiset(), multiset()),
						asSetAtom(SAILORS, constant(42L), set(), set(), set())));
	}

	@Test
	public void testFulOuterJoinEquality_on_left() throws Exception {
		String sql = "SELECT 1 FROM Sailors S FULL OUTER JOIN Reserves R\n"
				+ "ON (S.sid = R.sid AND S.sid = 42)";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(42L), multiset(), multiset(), multiset())),
				asView(asMultisetAtom(RESERVES, dist(), multiset(), multiset())));
	}

	@Test
	public void testFullOuterJoinEquality_on_right() throws Exception {
		String sql = "SELECT 1 FROM Sailors S FULL OUTER JOIN Reserves R\n"
				+ "ON (S.sid = R.sid AND R.sid = 42)";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())),
				asView(asMultisetAtom(RESERVES, constant(42L), multiset(), multiset())));
	}
}
