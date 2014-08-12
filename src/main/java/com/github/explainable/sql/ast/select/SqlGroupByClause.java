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
import com.github.explainable.sql.aggtype.AggTypeSystem;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.expression.SqlColumnReference;
import com.github.explainable.sql.ast.expression.SqlExpressionVisitor;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.Type;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Class that represents the {@code GROUP BY} clause of a SQL query.
 */
public final class SqlGroupByClause extends SqlSelect {
	private final ImmutableList<SqlColumnReference> references;

	public SqlGroupByClause(List<SqlColumnReference> references) {
		Preconditions.checkNotNull(references);
		Preconditions.checkArgument(!references.isEmpty());
		this.references = ImmutableList.copyOf(references);
	}

	@Override
	public void accept(SqlSelectVisitor visitor, SqlNode parent) {
		SqlExpressionVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			for (SqlColumnReference reference : references) {
				reference.accept(childVisitor, this);
			}
		}
		visitor.leave(this, parent);
	}

	@Override
	public Type typeCheckImpl() {
		return TypeSystem.table();
	}

	@Override
	public AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		return AggTypeSystem.nonAgg();
	}

	@Override
	public String toString() {
		return " GROUP BY " + Joiner.on(", ").join(references);
	}

	public ImmutableList<SqlColumnReference> references() {
		return references;
	}
}
