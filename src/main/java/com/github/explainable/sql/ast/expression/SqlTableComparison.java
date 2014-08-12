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

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.SchemaListType;
import com.github.explainable.sql.type.Type;
import com.google.common.base.Preconditions;

public final class SqlTableComparison extends SqlExpression {
	// TODO: Ensure that ANY and ALL are only used in the context of an (in)equality comparison.
	private final SqlExpression right;

	private final SqlTableComparisonKind kind;

	public SqlTableComparison(SqlExpression right, SqlTableComparisonKind kind) {
		this.right = Preconditions.checkNotNull(right);
		this.kind = Preconditions.checkNotNull(kind);
	}

	@Override
	public void accept(SqlExpressionVisitor visitor, SqlNode parent) {
		SqlExpressionVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			right.accept(childVisitor, this);
		}
		visitor.leave(this, parent);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		return right.getAggType();
	}

	@Override
	protected Type typeCheckImpl() {
		SchemaListType rightType = right.getType().coerceToSchemaList();
		if (rightType == null) {
			throw new SqlException("Must be list: " + rightType);
		}

		return rightType.columnType();
	}

	@Override
	public EqualityArg equalityArg() {
		return null;
	}

	public SqlExpression right() {
		return right;
	}

	public SqlTableComparisonKind kind() {
		return kind;
	}

	@Override
	public String toString() {
		return kind + " " + right;
	}

	public enum SqlTableComparisonKind {
		ANY,
		ALL
	}
}
