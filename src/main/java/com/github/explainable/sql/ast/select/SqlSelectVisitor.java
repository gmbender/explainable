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

package com.github.explainable.sql.ast.select;

import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.expression.SqlExpressionVisitor;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 10/15/13 Time: 9:40 AM To change this template
 * use File | Settings | File Templates.
 */
public interface SqlSelectVisitor {
	void visit(SqlFromBaseTable from, SqlNode parent);

	@Nullable
	SqlSelectVisitor enter(SqlFromSubSelect from, SqlNode parent);

	void leave(SqlFromSubSelect from, SqlNode parent);

	@Nullable
	SqlSelectVisitor enter(SqlPlainSelect select, SqlNode parent);

	void leave(SqlPlainSelect select, SqlNode parent);

	void visit(SqlSelectAllColumns allColumns, SqlNode parent);

	void visit(SqlSelectAllColumnsInTable columnsInTable, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlSelectColumn column, SqlNode parent);

	void leave(SqlSelectColumn column, SqlNode parent);

	@Nullable
	SqlSelectVisitor enter(SqlSetOperation operation, SqlNode parent);

	void leave(SqlSetOperation operation, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlWhereClause where, SqlNode parent);

	void leave(SqlWhereClause where, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlHavingClause having, SqlNode parent);

	void leave(SqlHavingClause having, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlGroupByClause groupBy, SqlNode parent);

	void leave(SqlGroupByClause groupBy, SqlNode parent);

	@Nullable
	SqlSelectVisitor enter(SqlFromClause from, SqlNode parent);

	void leave(SqlFromClause from, SqlNode parent);

	SqlSelectVisitor enter(SqlSelectItemsClause output, SqlNode parent);

	void leave(SqlSelectItemsClause output, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlOnClause on, SqlNode parent);

	void leave(SqlOnClause on, SqlNode parent);

	SqlSelectVisitor enter(SqlFromJoin join, SqlNode parent);

	void leave(SqlFromJoin join, SqlNode parent);
}
