package com.github.explainable.example.demo;

import com.github.explainable.corelang.View;
import com.github.explainable.sql.Schema;
import com.github.explainable.sql.pipeline.passes.ViewExtractionPipeline;
import com.github.explainable.sql.table.TypedRelation;
import com.github.explainable.sql.table.TypedRelationImpl;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.Select;

import javax.annotation.Nullable;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.github.explainable.corelang.Atom.asMultisetAtom;
import static com.github.explainable.corelang.Terms.constant;
import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.multiset;
import static com.github.explainable.corelang.View.asView;
import static com.github.explainable.sql.type.TypeSystem.numeric;
import static com.github.explainable.sql.type.TypeSystem.string;

/**
 * Demo of the SQL-to-atom extraction machinery using the Sailors-and-Boats database schema from the
 * Cow Book.
 */
public final class SailorsLabelerDemo implements Demo {
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

	private static final View SAILORS_WHOLE
			= asView(asMultisetAtom(SAILORS, dist(), dist(), dist(), dist()));

	private static final View SAILORS_LESS_SID
			= asView(asMultisetAtom(SAILORS, multiset(), dist(), dist(), dist()));

	private static final View SAILORS_LESS_SNAME
			= asView(asMultisetAtom(SAILORS, dist(), multiset(), dist(), dist()));

	private static final View SAILORS_LESS_RATING
			= asView(asMultisetAtom(SAILORS, dist(), dist(), multiset(), dist()));

	private static final View SAILORS_LESS_AGE
			= asView(asMultisetAtom(SAILORS, dist(), dist(), dist(), multiset()));

	private static final View SAILORS_ME
			= asView(asMultisetAtom(SAILORS, constant(1L), dist(), dist(), dist()));

	private static final View BOATS_WHOLE
			= asView(asMultisetAtom(BOATS, dist(), dist(), dist()));

	private static final View BOATS_LESS_BID
			= asView(asMultisetAtom(BOATS, multiset(), dist(), dist()));

	private static final View BOATS_LESS_BNAME
			= asView(asMultisetAtom(BOATS, dist(), multiset(), dist()));

	private static final View BOATS_LESS_COLOR
			= asView(asMultisetAtom(BOATS, dist(), dist(), multiset()));

	private static final View RESERVES_WHOLE
			= asView(asMultisetAtom(RESERVES, dist(), dist(), dist()));

	private static final View RESERVES_LESS_SID
			= asView(asMultisetAtom(RESERVES, multiset(), dist(), dist()));

	private static final View RESERVES_LESS_BID
			= asView(asMultisetAtom(RESERVES, dist(), multiset(), dist()));

	private static final View RESERVES_LESS_DAY
			= asView(asMultisetAtom(RESERVES, dist(), dist(), multiset()));

	private static final View RESERVES_ME
			= asView(asMultisetAtom(RESERVES, constant(1L), dist(), dist()));

	private CCJSqlParserManager parserManager;

	private ViewExtractionPipeline extractionPipeline;

	SailorsLabelerDemo() {
		this.parserManager = new CCJSqlParserManager();
		this.extractionPipeline = ViewExtractionPipeline.create(SCHEMA);
	}

	private void checkUsage(
			String columnText,
			View queryView,
			View securityView,
			View meView,
			Set<String> othersUsedColumns,
			Set<String> myUsedColumns) {
		if (!queryView.precedes(securityView)) {
			if (queryView.isCompatibleWith(meView)) {
				myUsedColumns.add(columnText);
			}
			if (!queryView.precedes(meView)) {
				othersUsedColumns.add(columnText);
			}
		}
	}

	private Set<String> findUnusedColumns(
			Set<String> myUsedColumns,
			Set<String> othersUsedColumns,
			String... allColumnNames) {
		Set<String> result = Sets.newLinkedHashSet();

		for (String name : allColumnNames) {
			if (!myUsedColumns.contains(name) && !othersUsedColumns.contains(name)) {
				result.add(name);
			}
		}

		return result;
	}

	private String formatList(Collection<String> items, String conjunction, boolean pluralize) {
		Preconditions.checkArgument(!items.isEmpty());
		StringBuilder builder = new StringBuilder();
		boolean hasElements = false;

		for (Iterator<String> iterator = items.iterator(); iterator.hasNext(); ) {
			String item = iterator.next();

			if (hasElements) {
				builder.append(iterator.hasNext() ? ", " : (" " + conjunction + " "));
			}

			if (pluralize) {
				builder.append(String.format(item.endsWith("s") ? "%ses" : "%ss", item));
			} else {
				builder.append(item);
			}

			hasElements = true;
		}

		return builder.toString();
	}

	@Nullable
	private String formattedSailorInfo(List<View> views) {
		Set<String> myUsedColumns = Sets.newLinkedHashSet();
		Set<String> othersUsedColumns = Sets.newLinkedHashSet();
		boolean usesSailors = false;

		for (View view : views) {
			if (view.precedes(SAILORS_WHOLE)) {
				usesSailors = true;

				checkUsage("Sailor ID", view, SAILORS_LESS_SID, SAILORS_ME,
						othersUsedColumns, myUsedColumns);

				checkUsage("name", view, SAILORS_LESS_SNAME, SAILORS_ME,
						othersUsedColumns, myUsedColumns);

				checkUsage("rating", view, SAILORS_LESS_RATING, SAILORS_ME,
						othersUsedColumns, myUsedColumns);

				checkUsage("age", view, SAILORS_LESS_AGE, SAILORS_ME,
						othersUsedColumns, myUsedColumns);
			}
		}

		if (!usesSailors) {
			return null;
		}

		StringBuilder output = new StringBuilder();
		if (myUsedColumns.isEmpty() && othersUsedColumns.isEmpty()) {
			output.append("The number of sailors in the database.");
		} else if (!myUsedColumns.isEmpty() && othersUsedColumns.isEmpty()) {
			output.append(String.format("My %s.", formatList(myUsedColumns, "and", false)));
		} else if (myUsedColumns.isEmpty() && !othersUsedColumns.isEmpty()) {
			output.append(String.format("Other sailors' %s.",
					formatList(othersUsedColumns, "and", true)));
		} else if (myUsedColumns.equals(othersUsedColumns)) {
			output.append(String.format("The %s of me and others.",
					formatList(myUsedColumns, "and", true)));
		} else {
			output.append(String.format("My %s, as well as other sailors' %s.",
					formatList(myUsedColumns, "and", false),
					formatList(othersUsedColumns, "and", true)));
		}

		Set<String> unusedColumns = findUnusedColumns(myUsedColumns, othersUsedColumns,
				"Sailor ID", "name", "rating", "age");
		if (!unusedColumns.isEmpty()) {
			output.append(String.format(" (But it doesn't look at the %s of sailors.)",
					formatList(unusedColumns, "or", true)));
		}

		return output.toString();
	}

	@Nullable
	private String formattedReservesInfo(List<View> views) {
		Set<String> myUsedColumns = Sets.newLinkedHashSet();
		Set<String> othersUsedColumns = Sets.newLinkedHashSet();
		boolean usesReserves = false;

		for (View view : views) {
			if (view.precedes(RESERVES_WHOLE)) {
				usesReserves = true;

				checkUsage("Sailor ID", view, RESERVES_LESS_SID, RESERVES_ME,
						othersUsedColumns, myUsedColumns);

				checkUsage("Boat ID", view, RESERVES_LESS_BID, RESERVES_ME,
						othersUsedColumns, myUsedColumns);

				checkUsage("day", view, RESERVES_LESS_DAY, RESERVES_ME,
						othersUsedColumns, myUsedColumns);
			}
		}

		if (!usesReserves) {
			return null;
		}

		StringBuilder output = new StringBuilder();
		if (myUsedColumns.isEmpty() && othersUsedColumns.isEmpty()) {
			output.append("The number of reservations in the database.");
		} else if (!myUsedColumns.isEmpty() && othersUsedColumns.isEmpty()) {
			output.append(String.format("The %s of my reservations.",
					formatList(myUsedColumns, "and", true)));
		} else if (myUsedColumns.isEmpty() && !othersUsedColumns.isEmpty()) {
			output.append(String.format("The %s of other sailors' reservations.",
					formatList(othersUsedColumns, "and", true)));
		} else if (myUsedColumns.equals(othersUsedColumns)) {
			output.append(String.format("The %s of my and others' reservations.",
					formatList(myUsedColumns, "and", true)));
		} else {
			output.append(
					String.format(
							"The %s of my reservations, as well as the %s of others' reservations.",
							formatList(myUsedColumns, "and", true),
							formatList(othersUsedColumns, "and", true)));
		}

		Set<String> unusedColumns = findUnusedColumns(myUsedColumns, othersUsedColumns,
				"Sailor ID", "Boat ID", "day");
		if (!unusedColumns.isEmpty()) {
			output.append(String.format(" (But it doesn't look at the %s of reservations.)",
					formatList(unusedColumns, "or", true)));
		}

		return output.toString();
	}

	@Nullable
	private String formattedBoatInfo(List<View> views) {
		Set<String> usedColumns = Sets.newLinkedHashSet();
		boolean usesBoats = false;

		for (View view : views) {
			if (view.precedes(BOATS_WHOLE)) {
				usesBoats = true;

				if (!view.precedes(BOATS_LESS_BID)) {
					usedColumns.add("ID");
				}

				if (!view.precedes(BOATS_LESS_BNAME)) {
					usedColumns.add("name");
				}

				if (!view.precedes(BOATS_LESS_COLOR)) {
					usedColumns.add("color");
				}
			}
		}

		if (!usesBoats) {
			return null;
		}

		StringBuilder output = new StringBuilder();
		if (usedColumns.isEmpty()) {
			output.append("The number of boats in the database.");
		} else {
			output.append(String.format("The %s of various boats.",
					formatList(usedColumns, "and", true)));
		}

		Set<String> unusedColumns = findUnusedColumns(usedColumns, ImmutableSet.<String>of(),
				"ID", "name", "color");

		if (!unusedColumns.isEmpty()) {
			output.append(String.format(" (But it doesn't look at the %s of boats.)",
					formatList(unusedColumns, "or", true)));
		}

		return output.toString();
	}

	private void analyzeAndPrint(List<View> views, DemoRunner runner) {
		List<String> outputLines = Lists.newArrayList();

		String sailorInfo = formattedSailorInfo(views);
		if (sailorInfo != null) {
			outputLines.add(sailorInfo);
		}

		String boatInfo = formattedBoatInfo(views);
		if (boatInfo != null) {
			outputLines.add(boatInfo);
		}

		String reservesInfo = formattedReservesInfo(views);
		if (reservesInfo != null) {
			outputLines.add(reservesInfo);
		}

		if (outputLines.isEmpty()) {
			runner.printEmptySection(
					"This query doesn't reveal any information about the database");
		} else {
			runner.printSection("The query looks at no more than the following information",
					outputLines);
		}
	}

	@Override
	public void showHelpMessage(DemoRunner runner) {
		runner.printSection("Schema", SCHEMA.relations());
		runner.printSection("Hints", ImmutableList.of(
				"Type a SQL query over the Sailors, Reserves, and Boats relations and press Enter.",
				"My Sailor ID (sid) is 1. Try using it in a query and see what happens ;-)",
				"Table and column names are case-sensitive."));
	}

	@Override
	public void handleQuery(String sql, DemoRunner runner) throws JSQLParserException {
		Select select = (Select) parserManager.parse(new StringReader(sql));
		List<View> views = extractionPipeline.execute(select);

		runner.printSection("Extracted views", views);
		analyzeAndPrint(views, runner);
	}

	@Override
	public void reset() {
		parserManager = new CCJSqlParserManager();
		extractionPipeline = ViewExtractionPipeline.create(SCHEMA);
	}

	public static void main(String[] args) {
		SwingDemoRunner.create(new SailorsLabelerDemo()).load();
	}
}
