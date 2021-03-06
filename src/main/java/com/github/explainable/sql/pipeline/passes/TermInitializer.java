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
import com.github.explainable.sql.ast.expression.SqlExists;
import com.github.explainable.sql.ast.expression.SqlExpressionVisitor;
import com.github.explainable.sql.ast.expression.SqlSubSelect;
import com.github.explainable.sql.ast.select.SqlFromBaseTable;
import com.github.explainable.sql.ast.select.SqlSelectAllColumns;
import com.github.explainable.sql.ast.select.SqlSelectAllColumnsInTable;
import com.github.explainable.sql.ast.select.SqlSelectColumn;
import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.github.explainable.sql.ast.select.SqlSelectVisitor;
import com.github.explainable.sql.pipeline.DependsOn;
import com.github.explainable.sql.pipeline.TransformationPass;
import com.github.explainable.sql.table.BaseColumn;
import com.github.explainable.sql.table.Column;

import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.multiset;

/**
 * Pass that walks through the AST of a SQL query and ensures that (i) every base column is
 * associated with a term that is at least a multiset variable, and (ii) every base column that is
 * explicitly referenced by a query is associated with a term that is at least distinguished.
 */
@DependsOn(ColumnResolver.class)
final class TermInitializer implements TransformationPass {
	@Override
	public void execute(SqlSelectStmt select) {
		select.accept(new FollowSelectItemsVisitor(), null);
	}

	private static abstract class BaseVisitor extends AbstractVisitor {
		@Override
		public void visit(SqlColumnReference reference, SqlNode parent) {
			Column column = reference.getColumn();
			if (column instanceof BaseColumn) {
				BaseColumn baseColumn = (BaseColumn) column;
				baseColumn.setTerm(baseColumn.getTerm().unifyWith(dist()));
			}
		}

		@Override
		public SqlExpressionVisitor enter(SqlExists exists, SqlNode parent) {
			exists.subSelect().select().accept(new IgnoreSelectItemsVisitor(), null);
			return null;
		}

		@Override
		public void visit(SqlFromBaseTable from, SqlNode parent) {
			for (BaseColumn column : from.getBaseTable().columns()) {
				column.setTerm(multiset());
			}
		}
	}

	private static final class IgnoreSelectItemsVisitor extends BaseVisitor {
		private IgnoreSelectItemsVisitor() {
		}

		@Override
		public SqlSelectVisitor enter(SqlSubSelect subSelect, SqlNode parent) {
			return new FollowSelectItemsVisitor();
		}

		@Override
		public SqlExpressionVisitor enter(SqlSelectColumn column, SqlNode parent) {
			// Avoid exploring the node's children.
			return null;
		}
	}

	private static class FollowSelectItemsVisitor extends BaseVisitor {
		@Override
		public void visit(SqlSelectAllColumns allColumns, SqlNode parent) {
			for (Column column : allColumns.getColumns()) {
				if (column instanceof BaseColumn) {
					BaseColumn baseColumn = (BaseColumn) column;
					baseColumn.setTerm(baseColumn.getTerm().unifyWith(dist()));
				}
			}
		}

		@Override
		public void visit(SqlSelectAllColumnsInTable columnsInTable, SqlNode parent) {
			for (Column column : columnsInTable.getColumns()) {
				if (column instanceof BaseColumn) {
					BaseColumn baseColumn = (BaseColumn) column;
					baseColumn.setTerm(baseColumn.getTerm().unifyWith(dist()));
				}
			}
		}
	}
}
