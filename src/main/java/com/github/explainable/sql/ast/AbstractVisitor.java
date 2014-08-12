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

package com.github.explainable.sql.ast;

import com.github.explainable.sql.ast.expression.*;
import com.github.explainable.sql.ast.select.*;

import javax.annotation.Nullable;

/**
 * A default visitor implementation that traverses the entire AST but doesn't do anything
 * interesting. You can override this method to perform AST manipulations that only do something
 * nontrivial on a small number of nodes.
 */
public abstract class AbstractVisitor implements SqlExpressionVisitor, SqlSelectVisitor {
	protected AbstractVisitor() {
	}

	@SuppressWarnings("UnusedParameters")
	protected AbstractVisitor defaultEnter(SqlNode node, SqlNode parent) {
		// Do nothing by default.
		return this;
	}

	@SuppressWarnings("UnusedParameters")
	protected void defaultLeave(SqlNode node, SqlNode parent) {
		// Do nothing by default.
	}

	@SuppressWarnings("UnusedParameters")
	protected void defaultVisit(SqlNode node, SqlNode parent) {
		// Do nothing by default.
	}

	@Override
	public SqlExpressionVisitor enter(SqlBinaryExpression expr, SqlNode parent) {
		return defaultEnter(expr, parent);
	}

	@Override
	public void leave(SqlBinaryExpression expr, SqlNode parent) {
		defaultLeave(expr, parent);
	}

	@Override
	public void visit(SqlColumnReference reference, SqlNode parent) {
		defaultVisit(reference, parent);
	}

	@Override
	public void visit(SqlCountAll count, SqlNode parent) {
		defaultVisit(count, parent);
	}

	@Override
	public SqlExpressionVisitor enter(SqlExists exists, SqlNode parent) {
		return defaultEnter(exists, parent);
	}

	@Override
	public void leave(SqlExists exists, SqlNode parent) {
		defaultLeave(exists, parent);
	}

	@Override
	public SqlExpressionVisitor enter(SqlIn in, SqlNode parent) {
		return defaultEnter(in, parent);
	}

	@Override
	public void leave(SqlIn in, SqlNode parent) {
		defaultLeave(in, parent);
	}

	@Override
	public SqlExpressionVisitor enter(SqlIsNull isNull, SqlNode parent) {
		return defaultEnter(isNull, parent);
	}

	@Override
	public void leave(SqlIsNull isNull, SqlNode parent) {
		defaultLeave(isNull, parent);
	}

	@Override
	public SqlExpressionVisitor enter(SqlList list, SqlNode parent) {
		return defaultEnter(list, parent);
	}

	@Override
	public void leave(SqlList list, SqlNode parent) {
		defaultLeave(list, parent);
	}

	@Override
	public SqlExpressionVisitor enter(SqlNegate negate, SqlNode parent) {
		return defaultEnter(negate, parent);
	}

	@Override
	public void leave(SqlNegate negate, SqlNode parent) {
		defaultLeave(negate, parent);
	}

	@Override
	public void visit(SqlNull sqlNull, SqlNode parent) {
		defaultVisit(sqlNull, parent);
	}

	@Override
	public void visit(SqlNumericConstant constant, SqlNode parent) {
		defaultVisit(constant, parent);
	}

	@Override
	public void visit(SqlStringConstant constant, SqlNode parent) {
		defaultVisit(constant, parent);
	}

	@Override
	public SqlSelectVisitor enter(SqlSubSelect subSelect, SqlNode parent) {
		return defaultEnter(subSelect, parent);
	}

	@Override
	public void leave(SqlSubSelect subSelect, SqlNode parent) {
		defaultLeave(subSelect, parent);
	}

	@Override
	public SqlExpressionVisitor enter(SqlTableComparison comparison, SqlNode parent) {
		return defaultEnter(comparison, parent);
	}

	@Override
	public void leave(SqlTableComparison comparison, SqlNode parent) {
		defaultLeave(comparison, parent);
	}

	@Override
	public SqlExpressionVisitor enter(SqlUnaryAggregate aggregate, SqlNode parent) {
		return defaultEnter(aggregate, parent);
	}

	@Override
	public void leave(SqlUnaryAggregate aggregate, SqlNode parent) {
		defaultLeave(aggregate, parent);
	}

	@Override
	public void visit(SqlFromBaseTable from, SqlNode parent) {
		defaultVisit(from, parent);
	}

	@Override
	public SqlSelectVisitor enter(SqlFromSubSelect from, SqlNode parent) {
		return defaultEnter(from, parent);
	}

	@Override
	public void leave(SqlFromSubSelect from, SqlNode parent) {
		defaultLeave(from, parent);
	}

	@Override
	public SqlSelectVisitor enter(SqlPlainSelect select, SqlNode parent) {
		return defaultEnter(select, parent);
	}

	@Override
	public void leave(SqlPlainSelect select, SqlNode parent) {
		defaultLeave(select, parent);
	}

	@Override
	public void visit(SqlSelectAllColumns allColumns, SqlNode parent) {
		defaultVisit(allColumns, parent);
	}

	@Override
	public void visit(SqlSelectAllColumnsInTable columnsInTable, SqlNode parent) {
		defaultVisit(columnsInTable, parent);
	}

	@Override
	public SqlExpressionVisitor enter(SqlSelectColumn column, SqlNode parent) {
		return defaultEnter(column, parent);
	}

	@Override
	public void leave(SqlSelectColumn column, SqlNode parent) {
		defaultLeave(column, parent);
	}

	@Override
	public SqlSelectVisitor enter(SqlSetOperation operation, SqlNode parent) {
		return defaultEnter(operation, parent);
	}

	@Override
	public void leave(SqlSetOperation operation, SqlNode parent) {
		defaultLeave(operation, parent);
	}

	@Override
	public SqlExpressionVisitor enter(SqlOnClause on, SqlNode parent) {
		return defaultEnter(on, parent);
	}

	@Override
	public void leave(SqlOnClause on, SqlNode parent) {
		defaultLeave(on, parent);
	}

	@Override
	public SqlSelectVisitor enter(SqlFromJoin join, SqlNode parent) {
		return defaultEnter(join, parent);
	}

	@Override
	public void leave(SqlFromJoin join, SqlNode parent) {
		defaultLeave(join, parent);
	}

	@Override
	public SqlExpressionVisitor enter(SqlWhereClause where, SqlNode parent) {
		return defaultEnter(where, parent);
	}

	@Override
	public void leave(SqlWhereClause where, SqlNode parent) {
		defaultLeave(where, parent);
	}

	@Override
	public SqlExpressionVisitor enter(SqlHavingClause having, SqlNode parent) {
		return defaultEnter(having, parent);
	}

	@Override
	public void leave(SqlHavingClause having, SqlNode parent) {
		defaultLeave(having, parent);
	}

	@Override
	public SqlExpressionVisitor enter(SqlGroupByClause groupBy, SqlNode parent) {
		return defaultEnter(groupBy, parent);
	}

	@Override
	public void leave(SqlGroupByClause groupBy, SqlNode parent) {
		defaultLeave(groupBy, parent);
	}

	@Nullable
	@Override
	public SqlSelectVisitor enter(SqlFromClause from, SqlNode parent) {
		return defaultEnter(from, parent);
	}

	@Override
	public void leave(SqlFromClause from, SqlNode parent) {
		defaultLeave(from, parent);
	}

	@Override
	public SqlSelectVisitor enter(SqlSelectItemsClause output, SqlNode parent) {
		return defaultEnter(output, parent);
	}

	@Override
	public void leave(SqlSelectItemsClause output, SqlNode parent) {
		defaultLeave(output, parent);
	}

	@Nullable
	@Override
	public SqlExpressionVisitor enter(SqlLike like, SqlNode parent) {
		return defaultEnter(like, parent);
	}

	@Override
	public void leave(SqlLike like, SqlNode parent) {
		defaultLeave(like, parent);
	}
}
