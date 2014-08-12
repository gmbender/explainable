package com.github.explainable.example.demo;

import com.github.explainable.corelang.Term;
import com.github.explainable.corelang.View;
import com.github.explainable.corelang.ViewToStringMode;
import com.github.explainable.labeler.policy.Policy;
import com.github.explainable.labeler.policy.PolicyLabeler;
import com.github.explainable.labeler.policy.PolicyMatcher;
import com.github.explainable.sql.Schema;
import com.github.explainable.sql.pipeline.passes.ViewExtractionPipeline;
import com.github.explainable.sql.table.TypedRelation;
import com.github.explainable.sql.table.TypedRelationImpl;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.Select;

import java.io.StringReader;
import java.util.List;
import java.util.Set;

import static com.github.explainable.corelang.Atom.asMultisetAtom;
import static com.github.explainable.corelang.Atom.asSetAtom;
import static com.github.explainable.corelang.Terms.constant;
import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.multiset;
import static com.github.explainable.corelang.View.asView;
import static com.github.explainable.sql.type.TypeSystem.numeric;
import static com.github.explainable.sql.type.TypeSystem.string;

/**
 * Explanation generation demo developed for SIGMOD 2014 talk.
 */
public final class FacebookExplanationDemo implements Demo {
	private static final TypedRelation USER = TypedRelationImpl.builder().setName("User")
			.addColumn("uid", numeric())
			.addColumn("name", string())
			.addColumn("email", string())
			.build();

	private static final TypedRelation FRIEND = TypedRelationImpl.builder().setName("Friend")
			.addColumn("uid1", numeric())
			.addColumn("uid2", numeric())
			.build();

	private static final Schema SCHEMA = Schema.of(USER, FRIEND);

	private static final View V1
			= asView("V1", asMultisetAtom(USER, dist(), dist(), dist()));

	private static final View V2
			= asView("V2", asMultisetAtom(USER, dist(), dist(), multiset()));

	private static final View V3
			= asView("V3", asMultisetAtom(USER, multiset(), dist(), dist()));

	private static final View V4
			= asView("V4", asMultisetAtom(USER, constant(4L), dist(), dist()));

	private static final Term USER_FRIEND_UID = dist();

	private static final View V5 = asView("V5",
			asMultisetAtom(USER, USER_FRIEND_UID, dist(), dist()),
			asSetAtom(FRIEND, constant(4L), USER_FRIEND_UID));

	private static final View V6 = asView("V6",
			asMultisetAtom(USER, USER_FRIEND_UID, dist(), dist()),
			asSetAtom(FRIEND, USER_FRIEND_UID, constant(4L)));

	private static final View V7
			= asView("V7", asMultisetAtom(FRIEND, dist(), dist()));

	private static final ImmutableList<View> SECURITY_VIEWS
			= ImmutableList.of(V1, V2, V3, V4, V5, V6, V7);

	private CCJSqlParserManager parserManager;

	private ViewExtractionPipeline extractionPipeline;

	private PolicyLabeler labeler;

	private FacebookExplanationDemo() {
		this.parserManager = new CCJSqlParserManager();
		this.extractionPipeline = ViewExtractionPipeline.create(SCHEMA);
		this.labeler = PolicyLabeler.create(SECURITY_VIEWS);
	}

	@Override
	public void showHelpMessage(DemoRunner runner) {
		runner.printSection("Schema", SCHEMA.relations());
	}

	@Override
	public void handleQuery(String sql, DemoRunner runner) throws JSQLParserException {
		Select select = (Select) parserManager.parse(new StringReader(sql));
		List<View> views = extractionPipeline.execute(select);

		// Generate and display a list of filter-project views.
		List<String> viewStrings = Lists.newArrayList();
		for (View view : views) {
			viewStrings.add(view.toString(ViewToStringMode.SIMPLIFIED));
		}
		runner.printSection("Filter-Project Queries", viewStrings);

		// Generate and display an associated policy formula.
		Policy policy = labeler.label(views).simplify();
		String policyString = policy.toString();

		List<String> securityViewStrings = Lists.newArrayList();
		for (View view : policy.match(new ViewAccumulator())) {
			securityViewStrings.add("where " + view.toString(ViewToStringMode.SIMPLIFIED));
		}

		runner.printSection("Explanation",
				ImmutableList.builder()
						.add(policyString)
						.add("")
						.addAll(securityViewStrings)
						.build());
	}

	@Override
	public void reset() {
		parserManager = new CCJSqlParserManager();
		extractionPipeline = ViewExtractionPipeline.create(SCHEMA);
		labeler = PolicyLabeler.create(SECURITY_VIEWS);
	}

	public static void main(String[] args) {
		SwingDemoRunner.create(new FacebookExplanationDemo()).load();
	}

	private static final class ViewAccumulator implements PolicyMatcher<Set<View>> {
		@Override
		public Set<View> matchFalse() {
			return ImmutableSet.of();
		}

		@Override
		public Set<View> matchTrue() {
			return ImmutableSet.of();
		}

		@Override
		public Set<View> matchView(View view) {
			return ImmutableSet.of(view);
		}

		@Override
		public Set<View> matchAnd(Policy left, Policy right) {
			return ImmutableSet.<View>builder()
					.addAll(left.match(this))
					.addAll(right.match(this))
					.build();
		}

		@Override
		public Set<View> matchOr(Policy left, Policy right) {
			return ImmutableSet.<View>builder()
					.addAll(left.match(this))
					.addAll(right.match(this))
					.build();
		}
	}
}
