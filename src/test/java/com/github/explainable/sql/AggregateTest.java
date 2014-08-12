package com.github.explainable.sql;

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
 * examples from Section 5.5 of the Cow Book (Ramakrishnan and Gehrke. Database Management Systems,
 * 3rd Ed.), which focuses on queries containing aggregation operators such as {@code COUNT} and
 * {@code SUM}. Section 5.5.1 is tested separately in {@link GroupByTest}.
 */
@SuppressWarnings("HardcodedLineSeparator")
public final class AggregateTest {
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
	 * Section 5.5, Q25: Find the average age of all sailors.
	 */
	@Test
	public void testQ25() throws Exception {
		String sql = "SELECT AVG(S.age) FROM Sailors S";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), multiset(), dist())));
	}

	/**
	 * Section 5.5, Q26: Find the average age of sailors with a rating of 10.
	 */
	@Test
	public void testQ26() throws Exception {
		String sql = "SELECT AVG(S.age) FROM Sailors S WHERE S.rating = 10";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), constant(10L), dist())));
	}

	/**
	 * Section 5.5, Q27: Find the name and age of the oldest sailor.
	 */
	@Test
	public void testQ27() throws Exception {
		String sql = "SELECT S.sname, S.age\n"
				+ "FROM Sailors S\n"
				+ "WHERE S.age = (SELECT MAX(S2.age) FROM Sailors S2)";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), dist(), multiset(), dist())),
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), multiset(), dist()),
						asSetAtom(SAILORS, set(), set(), set(), set())));
	}

	/**
	 * Taken from the Cow Book, Section 5.5, Q27: Find the name and age of the oldest sailor.
	 */
	@Test(expected = SqlException.class)
	public void testQ27_variant() throws Exception {
		String sql = "SELECT S.sname, MAX(S.age) FROM Sailors S";
		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	/**
	 * Section 5.5, Q28: Count the number of sailors.
	 */
	@Test
	public void testQ28() throws Exception {
		String sql = "SELECT COUNT(*) FROM Sailors S";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), multiset(), multiset())));
	}

	/**
	 * Section 5.5, Q29: Count the number of different sailor names.
	 */
	@Test
	public void testQ29() throws Exception {
		String sql = "SELECT COUNT(DISTINCT S.sname) FROM Sailors S";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), dist(), multiset(), multiset())));
	}

	/**
	 * Section 5.5, Q30: Find the names of sailors who are older than the oldest sailor with a rating
	 * of 10.
	 */
	@Test
	public void testQ30() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S\n"
				+ "WHERE S.age > (SELECT MAX(S2.age) FROM Sailors S2 WHERE S2.rating = 10)";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), dist(), multiset(), dist())),
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), constant(10L), dist()),
						asSetAtom(SAILORS, set(), set(), set(), set())));
	}

	/**
	 * Variant of Section 5.5, Q30 that uses {@code ALL} instead of {@code MAX}.
	 */
	@Test
	public void testQ30_all() throws Exception {
		String sql = "SELECT S.sname\n"
				+ "FROM Sailors S\n"
				+ "WHERE S.age > ALL (SELECT S2.age FROM Sailors S2 WHERE S2.rating = 10)";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), dist(), multiset(), dist())),
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), constant(10L), dist()),
						asSetAtom(SAILORS, set(), set(), set(), set())));
	}

	/**
	 * Query with missing GROUP BY clause. Contributed by Lucja Kot.
	 */
	@Test(expected = SqlException.class)
	public void testBadAggregate1() throws Exception {
		String sql = "SELECT  S.sname, MAX(S.age) FROM  Sailors S";

		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	/**
	 * Query that selects columns which aren't in the GROUP BY clause. Contributed by Lucja Kot.
	 */
	@Test(expected = SqlException.class)
	public void testBadAggregate2() throws Exception {
		String sql = "SELECT  S.rating, S.sname,  MIN(S.age) AS minage\n"
				+ "FROM  Sailors S\n"
				+ "GROUP BY  S.rating";

		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	/**
	 * Illegal and doesn't run in MySQL. Contributed by Lucja Kot.
	 */
	@Test(expected = SqlException.class)
	public void testBadAggregate3() throws Exception {
		String sql = "SELECT S.sname FROM Sailors S GROUP BY S.sname HAVING  S.rating=9";

		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	/**
	 * Illegal but actually safe since all buckets have size 1 if sid is primary key; does not run in
	 * MySQL, but runs and returns correct result in PostgreSQL. Contributed by Lucja Kot.
	 */
	@Test(expected = SqlException.class)
	public void testBadAggregate4() throws Exception {
		String sql = "SELECT S.sid FROM Sailors S GROUP BY S.sid HAVING S.rating = 9";

		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	/**
	 * Illegal and doesn't run in MySQL. Contributed by Lucja Kot.
	 */
	@Test(expected = SqlException.class)
	public void testBadAggregate5() throws Exception {
		String sql = "SELECT S.rating FROM Sailors S\n"
				+ "WHERE AVG (S.age) = (SELECT MIN(AVG(S2.age)) FROM Sailors S2)";

		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	@Test(expected = SqlException.class)
	public void testAggregateWithoutFrom() throws Exception {
		String sql = "SELECT COUNT(*)";

		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}
}
