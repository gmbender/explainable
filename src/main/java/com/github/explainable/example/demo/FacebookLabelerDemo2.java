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

package com.github.explainable.example.demo;

import com.github.explainable.corelang.Term;
import com.github.explainable.corelang.View;
import com.github.explainable.corelang.ViewToStringMode;
import com.github.explainable.sql.Schema;
import com.github.explainable.sql.pipeline.passes.ViewExtractionPipeline;
import com.github.explainable.sql.table.TypedRelation;
import com.github.explainable.sql.table.TypedRelationImpl;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
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
import static com.github.explainable.corelang.Atom.asSetAtom;
import static com.github.explainable.corelang.Terms.constant;
import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.multiset;
import static com.github.explainable.corelang.View.asView;
import static com.github.explainable.sql.type.TypeSystem.numeric;
import static com.github.explainable.sql.type.TypeSystem.primitive;
import static com.github.explainable.sql.type.TypeSystem.string;

/**
 * Demo of the SQL-to-atom extraction machinery using the Sailors-and-Boats database schema from the
 * Cow Book.
 */
public final class FacebookLabelerDemo2 implements Demo {
	private static final TypedRelation USER = TypedRelationImpl.builder().setName("User")
			.addColumn("uid", numeric())
			.addColumn("name", string())
			.addColumn("email", string())
			.addColumn("pic_square", primitive())
			.build();

	private static final TypedRelation FRIEND = TypedRelationImpl.builder().setName("Friend")
			.addColumn("uid1", numeric())
			.addColumn("uid2", numeric())
			.build();

	private static final Schema SCHEMA = Schema.of(USER, FRIEND);

	private static final View USER_WHOLE
			= asView(asMultisetAtom(USER, dist(), dist(), dist(), dist()));

	private static final View USER_LESS_UID
			= asView(asMultisetAtom(USER, multiset(), dist(), dist(), dist()));

	private static final View USER_LESS_NAME
			= asView(asMultisetAtom(USER, dist(), multiset(), dist(), dist()));

	private static final View USER_LESS_EMAIL
			= asView(asMultisetAtom(USER, dist(), dist(), multiset(), dist()));

	private static final View USER_LESS_PIC_SQUARE
			= asView(asMultisetAtom(USER, dist(), dist(), dist(), multiset()));

	private static final View USER_ME
			= asView(asMultisetAtom(USER, constant(4L), dist(), dist(), dist()));

	private static final Term USER_FRIEND_UID = dist();

	private static final View USER_FRIEND = asView(
			asMultisetAtom(USER, USER_FRIEND_UID, dist(), dist(), dist()),
			asSetAtom(FRIEND, constant(4L), USER_FRIEND_UID));

	private static final View FRIEND_WHOLE
			= asView(asMultisetAtom(FRIEND, dist(), dist()));

	private static final View FRIEND_LESS_UID2
			= asView(asMultisetAtom(FRIEND, dist(), multiset()));

	private static final View FRIEND_OF_ME1
			= asView(asMultisetAtom(FRIEND, constant(4L), dist()));

	private CCJSqlParserManager parserManager;

	private ViewExtractionPipeline extractionPipeline;

	private FacebookLabelerDemo2() {
		this.parserManager = new CCJSqlParserManager();
		this.extractionPipeline = ViewExtractionPipeline.create(SCHEMA);
	}

	private void checkUsage(
			String columnText,
			View queryView,
			View securityView,
			View meView,
			View friendView,
			Set<String> othersUsedColumns,
			Set<String> friendsUsedColumns,
			Set<String> myUsedColumns) {
		if (!queryView.precedes(securityView)) {
			if (queryView.isCompatibleWith(meView) && !queryView.precedes(friendView)) {
				myUsedColumns.add(columnText);
			}
			if (!queryView.precedes(meView)) {
				friendsUsedColumns.add(columnText);
			}
			if (!queryView.precedes(meView) && !queryView.precedes(friendView)) {
				othersUsedColumns.add(columnText);
			}
		}
	}

	private Set<String> findUnusedColumns(
			Set<String> myUsedColumns,
			Set<String> friendsUsedColumns,
			Set<String> othersUsedColumns,
			String... allColumnNames) {
		Set<String> result = Sets.newLinkedHashSet();

		for (String name : allColumnNames) {
			if (!myUsedColumns.contains(name)
					&& !friendsUsedColumns.contains(name)
					&& !othersUsedColumns.contains(name)) {
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
	private String formattedUserInfo(List<View> views) {
		Set<String> myUsedColumns = Sets.newLinkedHashSet();
		Set<String> friendsUsedColumns = Sets.newLinkedHashSet();
		Set<String> othersUsedColumns = Sets.newLinkedHashSet();
		boolean usesSailors = false;

		for (View view : views) {
			if (view.precedes(USER_WHOLE)
					&& (!view.precedes(USER_ME) || !view.precedes(USER_FRIEND))) {
				usesSailors = true;

				checkUsage("user ID", view, USER_LESS_UID, USER_ME, USER_FRIEND,
						othersUsedColumns, friendsUsedColumns, myUsedColumns);

				checkUsage("name", view, USER_LESS_NAME, USER_ME, USER_FRIEND,
						othersUsedColumns, friendsUsedColumns, myUsedColumns);

				checkUsage("email address", view, USER_LESS_EMAIL, USER_ME, USER_FRIEND,
						othersUsedColumns, friendsUsedColumns, myUsedColumns);

				checkUsage("profile picture", view, USER_LESS_PIC_SQUARE, USER_ME, USER_FRIEND,
						othersUsedColumns, friendsUsedColumns, myUsedColumns);
			}
		}

		if (!usesSailors) {
			return null;
		}

		StringBuilder output = new StringBuilder();
		boolean myInfo = !myUsedColumns.isEmpty();
		boolean friendsInfo = !friendsUsedColumns.isEmpty();
		boolean othersInfo = !othersUsedColumns.isEmpty();
		if (!myInfo && !friendsInfo && !othersInfo) {
			output.append("The number of users in the database.");
		} else if (!myInfo && !friendsInfo && othersInfo) {
			output.append(String.format("Other peoples' %s.",
					formatList(othersUsedColumns, "and", true)));
		} else if (!myInfo && friendsInfo && !othersInfo) {
			output.append(String.format("My friends' %s.",
					formatList(friendsUsedColumns, "and", true)));
		} else if (!myInfo && friendsInfo && othersInfo) {
			if (friendsUsedColumns.equals(othersUsedColumns)) {
				output.append(String.format("The %s of my friends and others.",
						formatList(friendsUsedColumns, "and", true)));
			} else {
				output.append(String.format("My friends' %s and other peoples' %s",
						formatList(friendsUsedColumns, "and", true),
						formatList(othersUsedColumns, "and", true)));
			}
		} else if (myInfo && !friendsInfo && !othersInfo) {
			output.append(String.format("My %s.",
					formatList(myUsedColumns, "and", false)));
		} else if (myInfo && !friendsInfo && othersInfo) {
			if (myUsedColumns.equals(othersUsedColumns)) {
				output.append(String.format("The %s of me and others.",
						formatList(myUsedColumns, "and", true)));
			} else {
				output.append(String.format("My %s and other peoples' %s.",
						formatList(myUsedColumns, "and", false),
						formatList(othersUsedColumns, "and", true)));
			}
		} else if (myInfo && friendsInfo && !othersInfo) {
			if (myUsedColumns.equals(friendsUsedColumns)) {
				output.append(String.format("The %s of me and my friends.",
						formatList(myUsedColumns, "and", true)));
			} else {
				output.append(String.format("My %s and my friends' %s.",
						formatList(myUsedColumns, "and", false),
						formatList(friendsUsedColumns, "and", true)));
			}
		} else {
			if (myUsedColumns.equals(friendsUsedColumns)
					&& myUsedColumns.equals(othersUsedColumns)) {
				output.append(String.format("The %s of me, my friends, and others.",
						formatList(myUsedColumns, "and", true)));
			} else {
				output.append(String.format("My %s, my friends' %s, and others' %s.",
						formatList(myUsedColumns, "and", false),
						formatList(friendsUsedColumns, "and", true),
						formatList(othersUsedColumns, "and", true)));
			}
		}

		Set<String> unusedColumns = findUnusedColumns(
				myUsedColumns, friendsUsedColumns, othersUsedColumns,
				"user ID", "name", "email address", "profile picture");
		if (!unusedColumns.isEmpty()) {
			output.append(String.format(" (But it doesn't look at %s.)",
					formatList(unusedColumns, "or", true)));
		}

		return output.toString();
	}

	@Nullable
	private String formattedFriendInfo(List<View> views) {
		boolean myFriendUid = false;
		boolean myFriendCount = false;
		boolean otherFriendUid = false;
		boolean otherFriendCount = false;

		for (View view : views) {
			if (view.precedes(FRIEND_WHOLE)) {
				if (view.isCompatibleWith(FRIEND_OF_ME1)) {
					myFriendCount = true;
					if (!view.precedes(FRIEND_LESS_UID2)) {
						myFriendUid = true;
					}
				}

				if (!view.precedes(FRIEND_OF_ME1)) {
					otherFriendCount = true;
					if (!view.precedes(FRIEND_LESS_UID2)) {
						otherFriendUid = true;
					}
				}
			}
		}

		if (!myFriendCount && !otherFriendCount) {
			return null;
		} else if (!myFriendCount && otherFriendCount) {
			if (otherFriendUid) {
				return "The user IDs of other users' friends (but not my friends).";
			} else {
				return "The number of friends that certain other users have.";
			}
		} else if (myFriendCount && !otherFriendCount) {
			if (myFriendUid) {
				return "The user IDs of my friends.";
			} else {
				return "The number of friends I have.";
			}
		} else { // myFriendCount && otherFriendCount
			if (myFriendUid || otherFriendUid) {
				// This is a bit of an oversimplification...
				return "The user IDs of friends of me and others.";
			} else {
				return "The number of friends that other users and I have.";
			}
		}
	}

	private void analyzeAndPrint(List<View> views, DemoRunner runner) {
		List<String> outputLines = Lists.newArrayList();

		String sailorInfo = formattedUserInfo(views);
		if (sailorInfo != null) {
			outputLines.add(sailorInfo);
		}

		String reservesInfo = formattedFriendInfo(views);
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
				"Type a SQL query over the User and Friend relations and press Enter.",
				"My user ID (uid) is 4. Try using it in a query and see what happens ;-)",
				"Table and column names are case-sensitive."));
	}

	@Override
	public void handleQuery(String sql, DemoRunner runner) throws JSQLParserException {
		Select select = (Select) parserManager.parse(new StringReader(sql));
		List<View> views = extractionPipeline.execute(select);

		List<String> viewStrings = Lists.newArrayListWithCapacity(views.size());
		for (View view : views) {
			viewStrings.add(view.toString(ViewToStringMode.SIMPLIFIED));
		}

		runner.printSection("Compiler output", viewStrings);
		analyzeAndPrint(views, runner);
	}

	@Override
	public void reset() {
		parserManager = new CCJSqlParserManager();
		extractionPipeline = ViewExtractionPipeline.create(SCHEMA);
	}

	public static void main(String[] args) {
		SwingDemoRunner.create(new FacebookLabelerDemo2()).load();
	}
}
