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
 * Unit tests that check for border cases in SQL query analysis. Many of these were based on old
 * (hopefully fixed) bugs in the code base.
 */
@SuppressWarnings("HardcodedLineSeparator")
public class KnownBugsTest {
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

	@Test
	public void testConstantConstraintInDifferentScope() throws Exception {
		String sql = "SELECT sid FROM Sailors S WHERE EXISTS\n"
				+ "(SELECT 1 FROM Boats WHERE S.sid = 42)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, constant(42L), multiset(), multiset(), multiset()),
						asSetAtom(BOATS, set(), set(), set())),
				asView(asMultisetAtom(BOATS, multiset(), multiset(), multiset()),
						asSetAtom(SAILORS, constant(42L), set(), set(), set())));
	}

	@Test
	public void testCountIsList() throws Exception {
		String sql = "SELECT 1 FROM Sailors S WHERE sid IN\n"
				+ "(SELECT COUNT(*) FROM Boats)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())),
				asView(asMultisetAtom(BOATS, multiset(), multiset(), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set())));
	}

	@Test
	public void testInnerJoinWhereConstraint() throws Exception {
		String sql = "SELECT 1 FROM Sailors JOIN Reserves WHERE Reserves.bid = 4";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, set(), constant(4L), set())),
				asView(asMultisetAtom(RESERVES, multiset(), constant(4L), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set())));
	}

	@Test
	public void testInnerJoinOnConstraint() throws Exception {
		String sql = "SELECT 1 FROM Sailors JOIN Reserves ON (Reserves.bid = 4)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, set(), constant(4L), set())),
				asView(asMultisetAtom(RESERVES, multiset(), constant(4L), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set())));
	}

	@Test
	public void testOuterJoinWhereConstraint() throws Exception {
		String sql = "SELECT 1 FROM Sailors LEFT OUTER JOIN Reserves WHERE Reserves.bid = 4";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, set(), constant(4L), set())),
				asView(asMultisetAtom(RESERVES, multiset(), constant(4L), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set())));
	}

	@Test
	public void testOuterJoinOnConstraint() throws Exception {
		String sql = "SELECT 1 FROM Sailors LEFT OUTER JOIN Reserves ON (Reserves.bid = 4)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), multiset(), multiset())),
				asView(asMultisetAtom(RESERVES, multiset(), constant(4L), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set())));
	}

	@Test
	public void testDisjunctiveConstraint() throws Exception {
		String sql = "SELECT 1 FROM Sailors S JOIN Reserves R WHERE (R.bid = 4) OR (R.bid = 5)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, set(), set(), set())),
				asView(asMultisetAtom(RESERVES, multiset(), dist(), multiset()),
						asSetAtom(SAILORS, set(), set(), set(), set())));
	}

	@Test
	public void testCorrelatedColumnBody() throws Exception {
		// Aggregate-checking should succeed because R.sid can be treated as a constant within the
		// body of the sub-select.
		String sql = "SELECT 1 FROM Sailors S WHERE EXISTS\n"
				+ "(SELECT 1 FROM Reserves R GROUP BY R.sid HAVING R.sid = S.sid)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, sid, set(), set())),
				asView(asMultisetAtom(RESERVES, sid, multiset(), multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set())));
	}

	@Test
	public void testJoinOrder1() throws Exception {
		// This query should be considered valid because joins are performed from left to right
		// (from a logical perspective, if not a physical perspective)
		String sql = "SELECT 1 FROM Sailors S\n"
				+ "JOIN Reserves R ON (S.sid = R.sid)\n"
				+ "JOIN Boats B ON (R.bid = B.bid)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		Term bid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, sid, bid, set()),
						asSetAtom(BOATS, bid, set(), set())),
				asView(asMultisetAtom(RESERVES, sid, bid, multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(BOATS, bid, set(), set())),
				asView(asMultisetAtom(BOATS, bid, multiset(), multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, bid, set())));
	}

	@Test
	public void testJoinOrder2() throws Exception {
		// This query should be considered valid because joins are performed from left to right
		// (from a logical perspective, if not a physical one), so the condition of the second join
		// can reference either table in the first join.
		String sql = "SELECT 1 FROM Sailors S JOIN Reserves R ON (S.sid = R.sid)\n"
				+ "JOIN Boats B ON (S.sid = B.bid)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term sid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, sid, multiset(), multiset(), multiset()),
						asSetAtom(RESERVES, sid, set(), set()),
						asSetAtom(BOATS, sid, set(), set())),
				asView(asMultisetAtom(RESERVES, sid, multiset(), multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(BOATS, sid, set(), set())),
				asView(asMultisetAtom(BOATS, sid, multiset(), multiset()),
						asSetAtom(SAILORS, sid, set(), set(), set()),
						asSetAtom(RESERVES, sid, set(), set())));
	}

	@Test(expected = SqlException.class)
	public void testJoinOrder3() throws Exception {
		// This query should not be considered valid because joins are performed from left to right
		// (from a logical perspective, if not a physical one), so the first join cannot reference
		// the table B from the second join.
		String sql = "SELECT 1 FROM Sailors S JOIN Reserves R ON (S.sid = B.bid)\n"
				+ "JOIN Boats B ON (R.bid = B.bid)";
		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}
}
