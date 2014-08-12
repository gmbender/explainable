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
import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.multiset;
import static com.github.explainable.corelang.View.asView;
import static com.github.explainable.sql.type.TypeSystem.numeric;
import static com.github.explainable.sql.type.TypeSystem.string;
import static com.github.explainable.util.MoreAsserts.assertEquivalentElements;

/**
 * Unit tests for LIMIT and OFFSET clauses.
 */
public final class LimitOffsetTest {
	private static final TypedRelation SAILORS = TypedRelationImpl.builder().setName("Sailors")
			.addColumn("sid", numeric())
			.addColumn("sname", string())
			.addColumn("rating", numeric())
			.addColumn("age", numeric())
			.build();


	private static final Schema SCHEMA = Schema.of(SAILORS);

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
	public void testLimit() throws Exception {
		String sql = "SELECT S.sid FROM Sailors S LIMIT 5";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())));
	}

	@Test
	public void testLimitAll() throws Exception {
		String sql = "SELECT S.sid FROM Sailors S LIMIT ALL";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())));
	}

	@Test(expected = SqlException.class)
	public void testOffset() throws Exception {
		String sql = "SELECT S.sid FROM Sailors S OFFSET 10";
		extractor.execute((Select) parser.parse(new StringReader(sql)));
	}

	@Test
	public void testLimitOffset() throws Exception {
		String sql = "SELECT S.sid FROM Sailors S LIMIT 5 OFFSET 10";
		List<View> views = extractor.execute((Select) parser.parse(new StringReader(sql)));

		assertEquivalentElements(views,
				asView(asMultisetAtom(SAILORS, dist(), multiset(), multiset(), multiset())));
	}
}
