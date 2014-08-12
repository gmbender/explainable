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

package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.select.SqlSelectVisitor;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/17/13 Time: 10:00 AM To change this template
 * use File | Settings | File Templates.
 */
public interface SqlExpressionVisitor {
	@Nullable
	SqlExpressionVisitor enter(SqlBinaryExpression expr, SqlNode parent);

	void leave(SqlBinaryExpression expr, SqlNode parent);

	void visit(SqlColumnReference reference, SqlNode parent);

	void visit(SqlCountAll count, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlExists exists, SqlNode parent);

	void leave(SqlExists exists, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlIn in, SqlNode parent);

	void leave(SqlIn in, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlIsNull isNull, SqlNode parent);

	void leave(SqlIsNull isNull, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlList list, SqlNode parent);

	void leave(SqlList list, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlNegate negate, SqlNode parent);

	void leave(SqlNegate negate, SqlNode parent);

	void visit(SqlNull sqlNull, SqlNode parent);

	void visit(SqlNumericConstant constant, SqlNode parent);

	void visit(SqlStringConstant constant, SqlNode parent);

	@Nullable
	SqlSelectVisitor enter(SqlSubSelect subSelect, SqlNode parent);

	void leave(SqlSubSelect subSelect, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlTableComparison comparison, SqlNode parent);

	void leave(SqlTableComparison comparison, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlUnaryAggregate aggregate, SqlNode parent);

	void leave(SqlUnaryAggregate aggregate, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlLike like, SqlNode parent);

	void leave(SqlLike like, SqlNode parent);
}
