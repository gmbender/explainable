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

package com.github.explainable.sql.pipeline.passes;

import com.github.explainable.sql.ast.AbstractVisitor;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.expression.SqlColumnReference;
import com.github.explainable.sql.ast.expression.SqlExpressionVisitor;
import com.github.explainable.sql.ast.expression.SqlSubSelect;
import com.github.explainable.sql.ast.select.SqlGroupByClause;
import com.github.explainable.sql.ast.select.SqlHavingClause;
import com.github.explainable.sql.ast.select.SqlPlainSelect;
import com.github.explainable.sql.ast.select.SqlSelectItemsClause;
import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.github.explainable.sql.ast.select.SqlSelectVisitor;
import com.github.explainable.sql.pipeline.DependsOn;
import com.github.explainable.sql.pipeline.TransformationPass;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.table.Column;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Pass that traverses the AST of a SQL query and ensures that it doesn't try to mix aggregates in
 * an illegal way, like putting {@code COUNT}(*) in a {@code WHERE} clause or putting a non-{@code
 * GROUP BY} base table column in a {@code HAVING} clause.
 */
@DependsOn(CorrelatedColumnFinder.class)
final class AggTypeChecker implements TransformationPass {
	@Override
	public void execute(SqlSelectStmt select) {
		select.accept(new InitialVisitor(AggTypeForColumn.allNonAggregate()), null);
	}

	private static final class InitialVisitor extends AbstractVisitor {
		private final AggTypeForColumn typeForColumn;

		InitialVisitor(AggTypeForColumn typeForColumn) {
			this.typeForColumn = typeForColumn;
		}

		@Override
		protected void defaultVisit(SqlNode node, SqlNode parent) {
			node.aggTypeCheck(typeForColumn);
		}

		@Override
		protected void defaultLeave(SqlNode node, SqlNode parent) {
			node.aggTypeCheck(typeForColumn);
		}

		@Override
		public SqlSelectVisitor enter(SqlSelectItemsClause output, SqlNode parent) {
			return new InitialVisitor(afterGroupBy((SqlPlainSelect) parent));
		}

		@Override
		public SqlExpressionVisitor enter(SqlHavingClause having, SqlNode parent) {
			return new InitialVisitor(afterGroupBy((SqlPlainSelect) parent));
		}

		private AggTypeForColumn afterGroupBy(SqlPlainSelect select) {
			Set<Column> groupByColumns = Sets.newHashSet();

			SqlGroupByClause groupBy = select.groupBy();
			if (groupBy != null) {
				for (SqlColumnReference reference : groupBy.references()) {
					groupByColumns.add(reference.getColumn());
				}
			}

			return typeForColumn.withAggregates(groupByColumns);
		}

		@Override
		public SqlSelectVisitor enter(SqlSubSelect subSelect, SqlNode parent) {
			return new InitialVisitor(typeForColumn.forSubQuery(subSelect.getCorrelatedColumns()));
		}
	}
}
