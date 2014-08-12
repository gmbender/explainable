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

import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.expression.SqlExpression;
import com.github.explainable.sql.ast.expression.SqlExpressionVisitor;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.Type;
import com.google.common.base.Preconditions;

/**
 * Class that represents the {@code HAVING} clause of a SQL query.
 */
public final class SqlHavingClause extends SqlSelect {
	private final SqlExpression body;

	public SqlHavingClause(SqlExpression body) {
		this.body = Preconditions.checkNotNull(body);
	}

	@Override
	public void accept(SqlSelectVisitor visitor, SqlNode parent) {
		SqlExpressionVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			body.accept(childVisitor, this);
		}
		visitor.leave(this, parent);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		return body.getAggType();
	}

	@Override
	protected Type typeCheckImpl() {
		return body.getType();
	}

	@Override
	public String toString() {
		return " HAVING " + body;
	}
}
