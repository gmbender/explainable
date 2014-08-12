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
 * examples from Section 5.2 of the Cow Book (Ramakrishnan and Gehrke. Database Management Systems,
 * 3rd Ed.), which focuses on very basic queries.
 */
@SuppressWarnings("HardcodedLineSeparator")
public final class SimpleTest {
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
	 * Section 5.2, Q15
	 */
	@Test
	public void testQ15() throws Exception {
		String sql = "SELECT DISTINCT S.sname, S.age FROM Sailors S";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), dist(), multiset(), dist())));
	}

	/**
	 * Section 5.2, Q11
	 */
	@Test
	public void testQ11() throws Exception {
		String sql = "SELECT S.sid, S.sname, S.rating, S.age\n"
				+ "FROM Sailors AS S\n"
				+ "WHERE S.rating > 7";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), dist(), dist(), dist())));
	}

	/**
	 * Section 5.2, Q1
	 */
	@Test
	public void testQ1() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S, Reserves R\n"
				+ "WHERE S.sid = R.sid AND R.bid = 103";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, dist(), multiset(), multiset()),
						asSetAtom(RESERVES, sid, constant(103L), set())),
				asView(asMultisetAtom(RESERVES, sid, constant(103L), multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set())));
	}

	/**
	 * Section 5.2.1, Q16
	 */
	@Test
	public void testQ16() throws Exception {
		String sql = "SELECT R.sid\n"
				+ "FROM Boats B, Reserves R\n"
				+ "WHERE B.bid = R.bid AND B.color = 'red'";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term bid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(BOATS, bid, multiset(), constant("red")),
						asSetAtom(RESERVES, set(), bid, set())),
				asView(asMultisetAtom(RESERVES, dist(), bid, multiset()),
						asSetAtom(BOATS, bid, set(), constant("red"))));
	}

	/**
	 * Section 5.2.1, Q2
	 */
	@Test
	public void testQ2() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S, Reserves R, Boats B\n"
				+ "WHERE S.sid = R.sid AND R.bid = B.bid AND B.color = 'red'";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		Term bid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, dist(), multiset(), multiset()),
						asSetAtom(RESERVES, sid, bid, multiset()),
						asSetAtom(BOATS, bid, set(), constant("red"))),
				asView(asMultisetAtom(RESERVES, sid, bid, multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(BOATS, bid, set(), constant("red"))),
				asView(asMultisetAtom(BOATS, bid, multiset(), constant("red")),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid, set())));
	}

	/**
	 * Section 5.2.1, Q3
	 */
	@Test
	public void testQ3() throws Exception {
		String sql = "SELECT B.color\n"
				+ "FROM Sailors S, Reserves R, Boats B\n"
				+ "WHERE S.sid = R.sid AND R.bid = B.bid AND S.sname = 'Lubber'";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		Term bid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, constant("Lubber"), multiset(), multiset()),
						asSetAtom(RESERVES, sid, bid, set()),
						asSetAtom(BOATS, bid, set(), set())),
				asView(asMultisetAtom(RESERVES, sid, bid, multiset()),
						asSetAtom(SAILORS, sid, constant("Lubber"), set(), set()),
						asSetAtom(BOATS, bid, set(), set())),
				asView(asMultisetAtom(BOATS, bid, multiset(), dist()),
						asSetAtom(SAILORS, sid, constant("Lubber"), set(), set()),
						asSetAtom(RESERVES, sid, bid, set())));
	}

	/**
	 * Section 5.2.1, Q4
	 */
	@Test
	public void testQ4() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S, Reserves R\n"
				+ "WHERE S.sid = R.sid";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, dist(), multiset(), multiset()),
						asSetAtom(RESERVES, sid, set(), set())),
				asView(asMultisetAtom(RESERVES, sid, multiset(), multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set())));
	}

	/**
	 * Section 5.2.2, Q17
	 */
	@Test
	public void testQ17() throws Exception {
		String sql = "SELECT S.sname, S.rating+1 AS rating\n"
				+ "FROM Sailors S, Reserves R1, Reserves R2\n"
				+ "WHERE S.sid = R1.sid AND S.sid = R2.sid\n"
				+ "      AND R1.day = R2.day AND R1.bid <> R2.bid";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		Term day = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, dist(), dist(), multiset()),
						asSetAtom(RESERVES, sid, day, set()),
						asSetAtom(RESERVES, sid, day, set())),
				asView(asMultisetAtom(RESERVES, sid, dist(), day),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, set(), day)),
				asView(asMultisetAtom(RESERVES, sid, dist(), day),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, set(), day)));
	}

	/**
	 * Section 5.2.2, Unnamed query that appears immediately after Q17
	 */
	@Test
	public void testQ17_variant() throws Exception {
		String sql = "SELECT S1.sname AS name1, S2.sname AS name2\n"
				+ "FROM Sailors S1, Sailors S2\n"
				+ "WHERE 2*S1.rating = S2.rating-1";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), dist(), dist(), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set())),
				asView(asMultisetAtom(SAILORS, multiset(), dist(), dist(), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set())));
	}

	@Test
	public void testSelectOne() throws Exception {
		String sql = "SELECT 1";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views);
	}

	@Test
	public void testSelectOnePlusOne() throws Exception {
		String sql = "SELECT 1+1";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views);
	}

	@Test
	public void testSelectNull() throws Exception {
		String sql = "SELECT NULL";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views);
	}

	@Test
	public void testTransitiveEquality_innerJoinsOnly() throws Exception {
		String sql = "SELECT 1 FROM Boats B1, Boats B2, Boats B3\n"
				+ "WHERE B1.bid = B2.bid AND B2.bid = B3.bid AND B3.bid = 1";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(BOATS, constant(1L), multiset(), multiset()),
						asSetAtom(BOATS, constant(1L), set(), set()),
						asSetAtom(BOATS, constant(1L), set(), set())),
				asView(asMultisetAtom(BOATS, constant(1L), multiset(), multiset()),
						asSetAtom(BOATS, constant(1L), set(), set()),
						asSetAtom(BOATS, constant(1L), set(), set())),
				asView(asMultisetAtom(BOATS, constant(1L), multiset(), multiset()),
						asSetAtom(BOATS, constant(1L), set(), set()),
						asSetAtom(BOATS, constant(1L), set(), set())));
	}

	@Test
	public void testTransitiveEquality_outerJoinsOnly() throws Exception {
		String sql = "SELECT 1 FROM Sailors S\n"
				+ "LEFT JOIN Reserves R ON (S.sid = R.sid)\n"
				+ "LEFT JOIN Boats B ON (R.bid = B.bid)\n"
				+ "WHERE S.sid = 1";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term bid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(1L), multiset(), multiset(), multiset())),
				asView(asMultisetAtom(RESERVES, constant(1L), bid, multiset()),
						asSetAtom(SAILORS, constant(1L), set(), set(), set())),
				asView(asMultisetAtom(BOATS, bid, multiset(), multiset()),
						asSetAtom(RESERVES, constant(1L), bid, set()),
						asSetAtom(SAILORS, constant(1L), set(), set(), set())));
	}

	@Test
	public void testLike() throws Exception {
		String sql = "SELECT 1 FROM Sailors S WHERE S.sname LIKE 'J%'";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), dist(), multiset(), multiset())));
	}

	@Test
	public void testNotLike() throws Exception {
		String sql = "SELECT 1 FROM Sailors S WHERE S.sname NOT LIKE 'J%'";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), dist(), multiset(), multiset())));
	}
}
