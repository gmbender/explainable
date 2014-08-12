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
 * examples from Section 5.5.1 and 5.5.2 of the Cow Book (Ramakrishnan and Gehrke. Database
 * Management Systems, 3rd Ed.), which focus on {@code GROUP BY} and {@code HAVING} clauses.
 */
@SuppressWarnings("HardcodedLineSeparator")
public final class GroupByTest {
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
	 * Section 5.5.1, Q31: Find the age of the youngest sailor for each rating level.
	 */
	@Test
	public void testQ31() throws Exception {
		String sql = "SELECT S.rating, MIN(S.age)\n"
				+ "FROM Sailors S\n"
				+ "GROUP BY S.rating";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), dist(), dist())));
	}

	/**
	 * Section 5.5, Q32: Find the age of the youngest sailor who is eligible to vote (i.e., is at least
	 * 18 years old) for each rating level with at least two such sailors.
	 */
	@Test
	public void testQ32() throws Exception {
		String sql = "SELECT S.rating, MIN(S.age) AS minage\n"
				+ "FROM Sailors S\n"
				+ "WHERE S.age >= 18\n"
				+ "GROUP BY S.rating\n"
				+ "HAVING COUNT(*) > 1";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), dist(), dist())));
	}

	/**
	 * Section 5.5.1; variant of Q32 with a modified {@code HAVING} clause. This query is valid in
	 * SQL:1999 but not SQL:1992.
	 */
	@Ignore("Currently not supported by CCJSqlParser")
	@Test
	public void testQ32_variant1() throws Exception {
		String sql = "SELECT S.rating, MIN(S.age) AS minage\n"
				+ "FROM Sailors S\n"
				+ "WHERE S.age >= 18\n"
				+ "GROUP BY S.rating\n"
				+ "HAVING COUNT(*) > 1 AND EVERY (S.age <= 60)";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), dist(), dist())));
	}

	/**
	 * Section 5.5.1; variant of the query above which is subtly different from the variant immediately
	 * above.
	 */
	@Test
	public void testQ32_variant2() throws Exception {
		String sql = "SELECT S.rating, MIN(S.age) AS minage\n"
				+ "FROM Sailors S\n"
				+ "WHERE S.age >= 18 AND S.age <= 60\n"
				+ "GROUP BY S.rating\n"
				+ "HAVING COUNT(*) > 1";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), dist(), dist())));
	}

	/**
	 * Section 5.5.2, Q33: For each red boat, find the number of reservations for this boat.
	 */
	@Test
	public void testQ33() throws Exception {
		String sql = "SELECT B.bid, COUNT(*) AS reservationcount\n"
				+ "FROM Boats B, Reserves R\n"
				+ "WHERE R.bid = B.bid AND B.color = 'red'\n"
				+ "GROUP BY B.bid";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term bid = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(BOATS, bid, multiset(), constant("red")),
						asSetAtom(RESERVES, set(), bid, set())),
				asView(asMultisetAtom(RESERVES, multiset(), bid, multiset()),
						asSetAtom(BOATS, bid, set(), constant("red"))));
	}

	/**
	 * Taken from the Cow Bok, Section 5.5.2, Variant of Q33. Fails because the {@code color} column
	 * appears in the {@code HAVING} clause but not in the {@code GROUP BY} clause.
	 */
	@Test(expected = SqlException.class)
	public void testQ33_variant() throws Exception {
		String sql = "SELECT B.bid, COUNT(*) AS reservationcount\n"
				+ "FROM Boats B, Reserves R\n"
				+ "WHERE R.bid = B.bid\n"
				+ "GROUP BY B.bid\n"
				+ "HAVING B.color = 'red'";

		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	/**
	 * Section 5.5.2, Q4: Find the average age of sailors for each rating level that has at least two
	 * sailor.
	 */
	@Test
	public void testQ34() throws Exception {
		String sql = "SELECT S.rating, AVG(S.age) AS avgage\n"
				+ "FROM Sailors S\n"
				+ "GROUP BY S.rating\n"
				+ "HAVING COUNT(*) > 1";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), dist(), dist())));
	}

	/**
	 * Section 5.5.2; Equivalent formation of Q34 that has a nested subquery inside a {@code HAVING}
	 * clause.
	 */
	@Test
	public void testQ34_variant() throws Exception {
		String sql = "SELECT S.rating, AVG(S.age) AS avgage\n"
				+ "FROM Sailors S\n"
				+ "GROUP BY S.rating\n"
				+ "HAVING 1 < (SELECT COUNT(*) FROM Sailors S2 WHERE S.rating = S2.rating)";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term rating = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), rating, dist())),
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), rating, multiset()),
						asSetAtom(SAILORS, set(), set(), rating, set())));
	}

	/**
	 * Section 5.5.2, Q35: Find the average age of sailors who are of voting age (i.e., at least 18
	 * years old) for each rating level that has at least two sailors.
	 */
	@Test
	public void testQ35() throws Exception {
		String sql = "SELECT S.rating, AVG(S.age) AS avgage\n"
				+ "FROM Sailors S\n"
				+ "WHERE S.age >= 18\n"
				+ "GROUP BY S.rating\n"
				+ "HAVING 1 < (SELECT COUNT(*) FROM Sailors S2 WHERE S.rating = S2.rating)";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term rating = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), rating, dist())),
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), rating, multiset()),
						asSetAtom(SAILORS, set(), set(), rating, set())));
	}

	/**
	 * Section 5.5.2, Q36: Find the average age of sailors who are of voting age (i.e., at least 18
	 * years old) for each rating level that has at least two such sailors.
	 */
	@Test
	public void testQ36() throws Exception {
		String sql = "SELECT S.rating, AVG(S.age) AS avgage\n"
				+ "FROM Sailors S\n"
				+ "WHERE S.age > 18\n"
				+ "GROUP BY S.rating\n"
				+ "HAVING 1 < (SELECT COUNT(*)\n"
				+ "            FROM Sailors S2\n"
				+ "            WHERE S.rating = S2.rating AND S2.age >= 18 )";

		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		Term rating = dist();
		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), rating, dist())),
				asView(asMultisetAtom(SAILORS, multiset(), multiset(), rating, dist()),
						asSetAtom(SAILORS, set(), set(), rating, set())));
	}

	/**
	 * Taken from the Cow Book, Section 5.5.3, Q37: Find those ratings for which the average age of
	 * sailors is the minimum over all ratings.
	 */
	@Test(expected = SqlException.class)
	public void testQ36_temp() throws Exception {
		String sql = "SELECT Temp.rating, Temp.avgage\n"
				+ "FROM Sailors S\n"
				+ "WHERE AVG(S.age) = (SELECT MIN(AVG(S2.age))\n"
				+ "                    FROM Sailors S2\n"
				+ "                    GROUP BY S2.rating)";

		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}
}
