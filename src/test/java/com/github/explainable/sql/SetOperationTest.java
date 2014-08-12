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
 * examples from Section 5.3 of the Cow Book (Ramakrishnan and Gehrke. Database Management Systems,
 * 3rd Ed.), which focuses on boolean predicates and set operations such as {@code UNION} and {@code
 * INTERSECT}.
 */
@SuppressWarnings("HardcodedLineSeparator")
public final class SetOperationTest {
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
	 * Section 5.3, Q5. Technically, this test overestimates information disclosure, since we don't
	 * capture the fact that the query only reveals information about red and green boats. However,
	 * expanding disjunctions into unions isn't a priority.
	 */
	@Test
	public void testQ5() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S, Reserves R, Boats B\n"
				+ "WHERE S.sid = R.sid AND R.bid = B.bid\n"
				+ "      AND (B.color = 'red' OR B.color = 'green')";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		Term bid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, dist(), multiset(), multiset()),
						asSetAtom(RESERVES, sid, bid, set()),
						asSetAtom(BOATS, bid, set(), set())),
				asView(asMultisetAtom(RESERVES, sid, bid, multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(BOATS, bid, set(), set())),
				asView(asMultisetAtom(BOATS, bid, multiset(), dist()),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid, set())));
	}

	/**
	 * Section 5.3, Q6
	 */
	@Test
	public void testQ6() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S, Reserves R1, Boats B1, Reserves R2, Boats B2\n"
				+ "WHERE S.sid = R1.sid AND R1.bid = B1.bid\n"
				+ "      AND S.sid = R2.sid AND R2.bid = B2.bid\n"
				+ "      AND B1.color = 'red' AND B2.color = 'green'";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		Term bid1 = dist();
		Term bid2 = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, dist(), multiset(), multiset()),
						asSetAtom(RESERVES, sid, bid1, set()),
						asSetAtom(BOATS, bid1, set(), constant("red")),
						asSetAtom(RESERVES, sid, bid2, set()),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(RESERVES, sid, bid1, multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(BOATS, bid1, set(), constant("red")),
						asSetAtom(RESERVES, sid, bid2, set()),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(BOATS, bid1, multiset(), constant("red")),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid1, set()),
						asSetAtom(RESERVES, sid, bid2, set()),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(RESERVES, sid, bid2, multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid1, set()),
						asSetAtom(BOATS, bid1, set(), constant("red")),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(BOATS, bid2, multiset(), constant("green")),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid1, set()),
						asSetAtom(BOATS, bid1, set(), constant("red")),
						asSetAtom(RESERVES, sid, bid2, set())));
	}

	/**
	 * Section 5.3, Q5 (Rewritten to use UNION)
	 */
	@Test
	public void testQ5_union() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S, Reserves R, Boats B\n"
				+ "WHERE S.sid = R.sid AND R.bid = B.bid AND B.color = 'red'\n"
				+ "UNION\n"
				+ "SELECT S2.sname\n"
				+ "FROM Sailors S2, Reserves R2, Boats B2\n"
				+ "WHERE S2.sid = R2.sid AND R2.bid = B2.bid AND B2.color = 'green'\n";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid1 = dist();
		Term bid1 = dist();
		Term sid2 = dist();
		Term bid2 = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid1, dist(), multiset(), multiset()),
						asSetAtom(RESERVES, sid1, bid1, set()),
						asSetAtom(BOATS, bid1, set(), constant("red"))),
				asView(asMultisetAtom(RESERVES, sid1, bid1, multiset()),
						asSetAtom(SAILORS, sid1, set(), set(), set()),
						asSetAtom(BOATS, bid1, set(), constant("red"))),
				asView(asMultisetAtom(BOATS, bid1, multiset(), constant("red")),
						asSetAtom(SAILORS, sid1, set(), set(), set()),
						asSetAtom(RESERVES, sid1, bid1, set())),
				asView(asMultisetAtom(SAILORS, sid2, dist(), multiset(), multiset()),
						asSetAtom(RESERVES, sid2, bid2, set()),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(RESERVES, sid2, bid2, multiset()),
						asSetAtom(SAILORS, sid2, set(), set(), set()),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(BOATS, bid2, multiset(), constant("green")),
						asSetAtom(SAILORS, sid2, set(), set(), set()),
						asSetAtom(RESERVES, sid2, bid2, set())));
	}

	/**
	 * Section 5.3, Q6 (Rewritten to use INTERSECT)
	 */
	@Test
	public void testQ6_intersect() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S, Reserves R, Boats B\n"
				+ "WHERE S.sid = R.sid AND R.bid = B.bid AND B.color = 'red'\n"
				+ "INTERSECT\n"
				+ "SELECT S2.sname\n"
				+ "FROM Sailors S2, Reserves R2, Boats B2\n"
				+ "WHERE S2.sid = R2.sid AND R2.bid = B2.bid AND B2.color = 'green'\n";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid1 = dist();
		Term bid1 = dist();
		Term sid2 = dist();
		Term bid2 = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid1, dist(), multiset(), multiset()),
						asSetAtom(RESERVES, sid1, bid1, set()),
						asSetAtom(BOATS, bid1, set(), constant("red"))),
				asView(asMultisetAtom(RESERVES, sid1, bid1, multiset()),
						asSetAtom(SAILORS, sid1, set(), set(), set()),
						asSetAtom(BOATS, bid1, set(), constant("red"))),
				asView(asMultisetAtom(BOATS, bid1, multiset(), constant("red")),
						asSetAtom(SAILORS, sid1, set(), set(), set()),
						asSetAtom(RESERVES, sid1, bid1, set())),
				asView(asMultisetAtom(SAILORS, sid2, dist(), multiset(), multiset()),
						asSetAtom(RESERVES, sid2, bid2, set()),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(RESERVES, sid2, bid2, multiset()),
						asSetAtom(SAILORS, sid2, set(), set(), set()),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(BOATS, bid2, multiset(), constant("green")),
						asSetAtom(SAILORS, sid2, set(), set(), set()),
						asSetAtom(RESERVES, sid2, bid2, set())));
	}

	/**
	 * Section 5.3, Q19. Technically, this test overestimates information disclosure. We don't actually
	 * need to join with Sailors because there's a foreign-key dependency from Boats to Sailors.
	 * However, modeling referential integrity constraints is currently not a priority.
	 */
	@Test
	public void testQ19() throws Exception {
		String sql = "SELECT S.sid\n"
				+ "FROM Sailors S, Reserves R, Boats B\n"
				+ "WHERE S.sid = R.sid AND R.bid = B.bid AND B.color = 'red'\n"
				+ "EXCEPT\n"
				+ "SELECT S2.sid\n"
				+ "FROM Sailors S2, Reserves R2, Boats B2\n"
				+ "WHERE S2.sid = R2.sid AND R2.bid = B2.bid AND B2.color = 'green'";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid1 = dist();
		Term bid1 = dist();
		Term sid2 = dist();
		Term bid2 = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid1, multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, sid1, bid1, set()),
						asSetAtom(BOATS, bid1, set(), constant("red"))),
				asView(asMultisetAtom(RESERVES, sid1, bid1, multiset()),
						asSetAtom(SAILORS, sid1, set(), set(), set()),
						asSetAtom(BOATS, bid1, set(), constant("red"))),
				asView(asMultisetAtom(BOATS, bid1, multiset(), constant("red")),
						asSetAtom(SAILORS, sid1, set(), set(), set()),
						asSetAtom(RESERVES, sid1, bid1, set())),
				asView(asMultisetAtom(SAILORS, sid2, multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, sid2, bid2, set()),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(RESERVES, sid2, bid2, multiset()),
						asSetAtom(SAILORS, sid2, set(), set(), set()),
						asSetAtom(BOATS, bid2, set(), constant("green"))),
				asView(asMultisetAtom(BOATS, bid2, multiset(), constant("green")),
						asSetAtom(SAILORS, sid2, set(), set(), set()),
						asSetAtom(RESERVES, sid2, bid2, set())));
	}

	/**
	 * A variant of Section 5.3, Q19 that removes extraneous references to the Sailors relation.
	 */
	@Test
	public void testQ19_simplified() throws Exception {
		String sql = "SELECT R.sid\n"
				+ "FROM Boats B, Reserves R\n"
				+ "WHERE R.bid = B.bid AND B.color = 'red'\n"
				+ "EXCEPT\n"
				+ "SELECT R2.sid\n"
				+ "FROM Boats B2, Reserves R2\n"
				+ "WHERE R2.bid = B2.bid AND B2.color = 'green'";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term bid1 = dist();
		Term bid2 = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(BOATS, bid1, multiset(), constant("red")),
						asSetAtom(RESERVES, set(), bid1, set())),
				asView(asMultisetAtom(RESERVES, dist(), bid1, multiset()),
						asSetAtom(BOATS, bid1, set(), constant("red"))),
				asView(asMultisetAtom(BOATS, bid2, multiset(), constant("green")),
						asSetAtom(RESERVES, set(), bid2, set())),
				asView(asMultisetAtom(RESERVES, dist(), bid2, multiset()),
						asSetAtom(BOATS, bid2, set(), constant("green"))));
	}

	/**
	 * Section 5.3, Q20.
	 */
	@Test
	public void testQ20() throws Exception {
		String sql = "SELECT S.sid\n"
				+ "FROM Sailors S\n"
				+ "WHERE S.rating = 10\n"
				+ "UNION\n"
				+ "SELECT R.sid\n"
				+ "FROM Reserves R\n"
				+ "WHERE R.bid = 104";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), constant(10L), multiset())),
				asView(asMultisetAtom(RESERVES, dist(), constant(104L), multiset())));
	}

	@Test
	public void testUnionWithNull1() throws Exception {
		String sql = "SELECT 1 UNION SELECT NULL";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views);
	}

	@Test
	public void testUnionWithNull2() throws Exception {
		String sql = "SELECT NULL UNION SELECT 1";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views);
	}

	@Test
	public void testIntersectWithNull1() throws Exception {
		String sql = "SELECT 1 INTERSECT SELECT NULL";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views);
	}

	@Test
	public void testIntersectWithNull2() throws Exception {
		String sql = "SELECT NULL INTERSECT SELECT 1";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views);
	}

	@Test
	public void testExceptWithNull1() throws Exception {
		String sql = "SELECT 1 EXCEPT SELECT NULL";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views);
	}

	@Test
	public void testExceptWithNull2() throws Exception {
		String sql = "SELECT NULL EXCEPT SELECT 1";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views);
	}
}
