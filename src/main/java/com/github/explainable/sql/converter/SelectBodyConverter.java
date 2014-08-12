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

package com.github.explainable.sql.converter;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.ast.expression.SqlColumnReference;
import com.github.explainable.sql.ast.expression.SqlExpression;
import com.github.explainable.sql.ast.select.SqlFrom;
import com.github.explainable.sql.ast.select.SqlFromJoin;
import com.github.explainable.sql.ast.select.SqlFromJoin.SqlJoinKind;
import com.github.explainable.sql.ast.select.SqlOnClause;
import com.github.explainable.sql.ast.select.SqlPlainSelect;
import com.github.explainable.sql.ast.select.SqlPlainSelect.Builder;
import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.github.explainable.sql.ast.select.SqlSetOperation;
import com.github.explainable.sql.ast.select.SqlSetOperation.SqlSetOperationKind;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperation;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;

import javax.annotation.Nullable;

import static com.github.explainable.sql.converter.ConverterUtils.checkUnsupportedFeature;
import static com.github.explainable.sql.converter.ConverterUtils.checkUnsupportedFlag;

final class SelectBodyConverter implements SelectVisitor {
	private final MasterConverter master;

	@Nullable
	private SqlSelectStmt result;

	SelectBodyConverter(MasterConverter master) {
		this.master = master;
		this.result = null;
	}

	@Nullable
	public SqlSelectStmt convert(SelectBody selectBody) {
		// This silly dance is necessary to ensure that none of the accept(...) methods forget to
		// set the "result" variable. The code would be so much cleaner if accept(...) had a non-void
		// return type. Sigh.
		result = null;
		selectBody.accept(this);
		Preconditions.checkNotNull(result);
		SqlSelectStmt realResult = result;
		result = null;
		return realResult;
	}

	private SqlJoinKind joinType(Join join) {
		if (join.isNatural()) {
			throw new SqlException("Natural joins are not yet supported");
		}

		// Inner joins
		boolean simple = join.isSimple();
		boolean inner = join.isInner();

		// Outer joins
		boolean left = join.isLeft();
		boolean right = join.isRight();
		boolean full = join.isFull();
		boolean outer = join.isOuter();

		// Cross joins
		boolean cross = join.isCross();

		if (simple || inner) {
			if (left || right || full || outer || cross) {
				throw new SqlException("Unrecognized Join Type: " + join);
			}
			return SqlJoinKind.INNER;
		} else if (left) {
			if (right || full || cross) {
				throw new SqlException("Unrecognized Join Type: " + join);
			}
			return SqlJoinKind.LEFT_OUTER;
		} else if (right) {
			if (full || cross) {
				throw new SqlException("Unrecognized Join Type: " + join);
			}
			return SqlJoinKind.RIGHT_OUTER;
		} else if (full) {
			if (cross) {
				throw new SqlException("Unrecognized Join Type: " + join);
			}
			return SqlJoinKind.FULL_OUTER;
		} else if (cross) {
			if (outer || full) {
				throw new SqlException("Unrecognized Join Type: " + join);
			}
			// A cross join is just an inner join without a "WHERE" clause, although this constraint
			// might or might not be enforced by the DBMS.
			return SqlJoinKind.INNER;
		} else if (outer) {
			throw new SqlException("Outer join must be qualified with LEFT, RIGHT, or FULL");
		} else {
			// Not an outer or a cross join; must be an inner join.
			return SqlJoinKind.INNER;
		}
	}

	private SqlFrom convertJoin(SqlFrom left, Join join) {
		checkUnsupportedFeature(join.getUsingColumns(), "USING");

		SqlFrom right = master.convert(join.getRightItem());
		SqlJoinKind joinType = joinType(join);

		SqlOnClause on = null;
		if (join.getOnExpression() != null) {
			on = new SqlOnClause(master.convert(join.getOnExpression()));
		}

		return new SqlFromJoin(left, right, on, joinType);
	}

	private SqlColumnReference asColumn(SqlExpression expression) {
		if (expression instanceof SqlColumnReference) {
			return (SqlColumnReference) expression;
		} else {
			throw new SqlException("Expression: " + expression + " is not a table column");
		}
	}

	private void processLimit(Limit limit, Builder builder) {
		checkUnsupportedFlag(limit.isOffsetJdbcParameter(), "OFFSET JDBC Parameter");
		checkUnsupportedFlag(limit.isRowCountJdbcParameter(), "LIMIT JDBC Parameter");

		if (!limit.isLimitAll()) {
			checkUnsupportedFlag(limit.getRowCount() == 0, "LIMIT row count cannot be zero");
			builder.setLimit(limit.getRowCount());
		}

		if (limit.getOffset() != 0) {
			builder.setOffset(limit.getOffset());
		}
	}

	@Override
	public void visit(PlainSelect plainSelect) {
		checkUnsupportedFeature(plainSelect.getInto(), "INTO");
		checkUnsupportedFeature(plainSelect.getOrderByElements(), "ORDER BY");
		checkUnsupportedFeature(plainSelect.getTop(), "TOP");

		Builder builder = SqlPlainSelect.builder();

		// SELECT selectItems
		for (SelectItem selectItem : plainSelect.getSelectItems()) {
			builder.addSelectItem(master.convert(selectItem));
		}

		// DISTINCT
		if (plainSelect.getDistinct() != null) {
			checkUnsupportedFeature(plainSelect.getDistinct().getOnSelectItems(),
					"SELECT DISTINCT ON");

			builder.setDistinct(true);
		} else {
			builder.setDistinct(false);
		}

		// FROM from JOIN joins
		if (plainSelect.getFromItem() == null) {
			Preconditions.checkState(plainSelect.getJoins() == null);
		} else {
			SqlFrom from = master.convert(plainSelect.getFromItem());

			if (plainSelect.getJoins() != null) {
				for (Join join : plainSelect.getJoins()) {
					from = convertJoin(from, join);
				}
			}

			builder.setFrom(from);
		}

		// WHERE where
		if (plainSelect.getWhere() != null) {
			builder.setWhere(master.convert(plainSelect.getWhere()));
		}

		// GROUP BY
		if (plainSelect.getGroupByColumnReferences() != null) {
			for (Expression expression : plainSelect.getGroupByColumnReferences()) {
				builder.addGroupBy(asColumn(master.convert(expression)));
			}
		}

		// HAVING
		if (plainSelect.getHaving() != null) {
			builder.setHaving(master.convert(plainSelect.getHaving()));
		}

		// LIMIT
		if (plainSelect.getLimit() != null) {
			processLimit(plainSelect.getLimit(), builder);
		}

		result = builder.build();
	}

	@Override
	public void visit(SetOperationList setOperationList) {
		checkUnsupportedFeature(setOperationList.getLimit(), "SetOperationList LIMIT");
		checkUnsupportedFeature(setOperationList.getOrderByElements(),
				"SetOperationsList ORDER BY");

		int operationCount = setOperationList.getOperations().size();
		int plainSelectCount = setOperationList.getPlainSelects().size();

		if (operationCount == 0) {
			throw new SqlException("Expected at least one operation: " + setOperationList);
		}

		if (operationCount != plainSelectCount - 1) {
			throw new SqlException("Number of operations: " + operationCount
					+ " must be one less than number of operands: " + plainSelectCount);
		}

		// Assume that all operations have equal precedence, and that operators are executed
		// from left to right.
		SqlSelectStmt accumulator = convert(setOperationList.getPlainSelects().get(0));

		for (int i = 1; i < plainSelectCount; i++) {
			SqlSelectStmt right = convert(setOperationList.getPlainSelects().get(i));
			SqlSetOperationKind type = convertType(setOperationList.getOperations().get(i - 1));
			accumulator = new SqlSetOperation(accumulator, right, type);
		}

		result = accumulator;
	}

	@Override
	public void visit(WithItem withItem) {
		throw new SqlException("Unsupported Feature: WITH");
	}

	private SqlSetOperationKind convertType(SetOperation setOperation) {
		// JSqlParser is a bit broken here. We can't actually get its type by examining it, so we do
		// the evil thing: convert it to a string and then check the string's value.
		String opString = setOperation.toString();

		if (opString.equals("INTERSECT")) {
			return SqlSetOperationKind.INTERSECT;
		} else if (opString.equals("EXCEPT")) {
			return SqlSetOperationKind.EXCEPT;
		} else if (opString.equals("MINUS")) {
			return SqlSetOperationKind.EXCEPT;
		} else if (opString.equals("UNION")) {
			return SqlSetOperationKind.UNION;
		} else {
			throw new SqlException("Unknown set operation: " + opString);
		}
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("result", result)
				.toString();
	}
}
