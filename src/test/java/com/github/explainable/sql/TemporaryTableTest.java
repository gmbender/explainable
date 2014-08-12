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
import org.junit.Ignore;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static com.github.explainable.corelang.Atom.asMultisetAtom;
import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.multiset;
import static com.github.explainable.corelang.View.asView;
import static com.github.explainable.sql.type.TypeSystem.numeric;
import static com.github.explainable.sql.type.TypeSystem.string;
import static com.github.explainable.util.MoreAsserts.assertEquivalentElements;

/**
 * Unit tests for analysis of SQL queries containing temporary tables.
 */
@SuppressWarnings("HardcodedLineSeparator")
public class TemporaryTableTest {
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
	public void testTemp_validName1() throws Exception {
		String sql = "SELECT sid FROM (SELECT sid FROM Sailors S) AS Temp";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())));
	}

	@Test
	public void testTemp_validName2() throws Exception {
		String sql = "SELECT sid FROM (SELECT * FROM Sailors S) AS Temp";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), dist(), dist(), dist())));
	}

	@Test
	public void testTemp_validName3() throws Exception {
		String sql = "SELECT Temp.sid FROM (SELECT sid FROM Sailors S) AS Temp";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())));
	}

	@Test
	public void testTemp_validRename_short() throws Exception {
		String sql = "SELECT m_sid FROM (SELECT sid AS m_sid FROM Sailors S) AS Temp";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())));
	}

	@Test
	public void testTemp_validRename_full() throws Exception {
		String sql = "SELECT Temp.m_sid FROM (SELECT sid AS m_sid FROM Sailors S) AS Temp";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())));
	}

	@Test(expected = SqlException.class)
	public void testTemp_invalidRename_short() throws Exception {
		String sql = "SELECT sid FROM (SELECT sid AS m_sid FROM Sailors S) AS Temp";
		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	@Test(expected = SqlException.class)
	public void testTemp_invalidRename_full() throws Exception {
		String sql = "SELECT Temp.sid FROM (SELECT sid AS m_sid FROM Sailors S) AS Temp";
		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	@Test(expected = SqlException.class)
	public void testTemp_invalidName_bogus_short() throws Exception {
		String sql = "SELECT bogus FROM (SELECT sid FROM Sailors S) AS Temp";
		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	@Test(expected = SqlException.class)
	public void testTemp_invalidName_bogus_full() throws Exception {
		String sql = "SELECT Temp.bogus FROM (SELECT sid FROM Sailors S) AS Temp";
		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	@Test(expected = SqlException.class)
	public void testTemp_invalidName_selectAll_short() throws Exception {
		String sql = "SELECT bogus FROM (SELECT * FROM Sailors S) AS Temp";
		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	@Test(expected = SqlException.class)
	public void testTemp_invalidName_selectAll_full() throws Exception {
		String sql = "SELECT Temp.bogus FROM (SELECT * FROM Sailors S) AS Temp";
		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	@Test(expected = SqlException.class)
	public void testTemp_invalidName_projected_short() throws Exception {
		String sql = "SELECT sname FROM (SELECT sid FROM Sailors S) AS Temp";
		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	@Test(expected = SqlException.class)
	public void testTemp_invalidName_projected_full() throws Exception {
		String sql = "SELECT Temp.sname FROM (SELECT sid FROM Sailors S) AS Temp";
		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	@Test
	public void testTemp_successfulTypeCheck() throws Exception {
		// Type-checks because the "sid" column has type numeric().
		String sql = "SELECT Temp.sid FROM (SELECT sid FROM Sailors) AS Temp\n"
				+ "WHERE Temp.sid < 42";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())));
	}

	@Test(expected = SqlException.class)
	public void testTemp_failedTypeCheck() throws Exception {
		// Fails to type-checks because the "sname" column has type string().
		String sql = "SELECT Temp.sname FROM (SELECT sname FROM Sailors) AS Temp\n"
				+ "WHERE Temp.sname < 42";
		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	/**
	 * Section 5.5.2; Variant of Q36 that uses a temporary table instead of {@code HAVING}.
	 */
	@Test
	public void testQ36_temp() throws Exception {
		String sql = "SELECT Temp.rating, Temp.avgage\n"
				+ "FROM (SELECT S.rating, AVG(S.age) AS avgage, COUNT(*) AS ratingcount\n"
				+ "      FROM Sailors S\n"
				+ "      WHERE S.age > 18\n"
				+ "      GROUP BY S.rating) AS Temp\n"
				+ "WHERE Temp.ratingcount > 1";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), dist(), dist())));
	}

	/**
	 * Section 5.5.2; Variant of Q37 that finds those ratings for which the average age of sailors is
	 * the minimum over all ratings.
	 */
	@Ignore("Doesn't work in Postgres or MySQL")
	@Test
	public void testQ37() throws Exception {
		String sql = "SELECT Temp.rating, Temp.avgage\n"
				+ "FROM (SELECT S.rating, AVG(S.age) AS avgage\n"
				+ "      FROM Sailors S\n"
				+ "      GROUP BY S.rating) AS Temp\n"
				+ "WHERE Temp.avgage = (SELECT MIN(Temp.avgage) FROM Temp)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), dist(), dist())),
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), multiset(), dist())));
	}

	/**
	 * Legal but does not run in either MySQL or PostgreSQL for some weird reason, possibly because
	 * they don't like aggregates on temporary tables. Provided by Lucja Kot.
	 */
	@Ignore("Doesn't work in Postgres or MySQL")
	@Test
	public void testQ37_variant1() throws Exception {
		String sql = "SELECT Temp.rating\n"
				+ "FROM (SELECT S.rating, AVG(S.age) AS avgage\n"
				+ "      FROM Sailors S\n"
				+ "      GROUP BY S.rating) AS Temp\n"
				+ "WHERE Temp.avgage = (SELECT MIN(Temp.avgage) FROM Temp)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), dist(), dist())));
	}

	/**
	 * Alternate version of the above that runs in both MySQL and Postgres. Provided by Lucja Kot.
	 */
	@Test
	public void testQ37_variant2() throws Exception {
		String sql = "SELECT Temp.rating\n"
				+ "FROM (SELECT S.rating , AVG(S.age) AS avgage\n"
				+ "      FROM Sailors S\n"
				+ "      GROUP BY S.rating) AS Temp\n"
				+ "WHERE Temp.avgage = (SELECT MIN(Temp2.avgage)\n"
				+ "                     FROM (SELECT  S2.rating, AVG (S2.age) AS avgage\n"
				+ "                           FROM  Sailors S2\n"
				+ "                           GROUP BY  S2.rating) AS Temp2)";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), dist(), dist())),
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), dist(), dist())));
	}
}
