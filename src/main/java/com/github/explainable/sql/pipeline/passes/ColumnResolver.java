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

import com.github.explainable.sql.Schema;
import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.ast.AbstractVisitor;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.expression.SqlColumnReference;
import com.github.explainable.sql.ast.select.SqlFromBaseTable;
import com.github.explainable.sql.ast.select.SqlFromSubSelect;
import com.github.explainable.sql.ast.select.SqlPlainSelect;
import com.github.explainable.sql.ast.select.SqlSelectAllColumns;
import com.github.explainable.sql.ast.select.SqlSelectAllColumnsInTable;
import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.github.explainable.sql.ast.select.SqlSelectVisitor;
import com.github.explainable.sql.pipeline.TransformationPass;
import com.github.explainable.sql.table.Column;
import com.github.explainable.sql.table.NestedScope;
import com.github.explainable.sql.table.TypedRelation;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Pass that adds two different types of annotations to the AST. First, it associates a  {@link
 * NestedScope} object with each (possibly nested) {@code SELECT} statement that appears in the
 * specified AST. And second, it associates a {@link Column} object with every AST node that
 * references one or more table columns.
 */
final class ColumnResolver implements TransformationPass {
	private final Schema schema;

	ColumnResolver(Schema schema) {
		this.schema = Preconditions.checkNotNull(schema);
	}

	@Override
	public void execute(SqlSelectStmt select) {
		select.accept(new InitialVisitor(), null);
	}

	private final class InitialVisitor extends AbstractVisitor {
		@Override
		public SqlSelectVisitor enter(SqlPlainSelect select, SqlNode parent) {
			NestedScope scope = NestedScope.create(select, null);
			select.setScope(scope);
			return new ScopedVisitor(scope);
		}
	}

	private class ScopedVisitor extends AbstractVisitor {
		private final NestedScope scope;

		private ScopedVisitor(NestedScope scope) {
			this.scope = scope;
		}

		@Override
		public void visit(SqlColumnReference reference, SqlNode parent) {
			Column tableColumn = scope.findColumn(
					reference.tableAlias(),
					reference.columnName());

			reference.setColumn(tableColumn);
		}

		@Override
		public void visit(SqlFromBaseTable from, SqlNode parent) {
			TypedRelation relation = schema.findRelation(from.tableName());
			if (relation == null) {
				throw new SqlException("Couldn't find table: " + from.tableName());
			}

			String tableAlias = from.alias();
			if (tableAlias == null) {
				tableAlias = from.tableName();
			}

			from.setBaseTable(scope.createBaseTable(relation, tableAlias));
		}

		@Override
		public void leave(SqlFromSubSelect from, SqlNode parent) {
			scope.createTemporaryTable(from.body(), from.alias());
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this)
					.add("scope", scope)
					.toString();
		}

		@Override
		public SqlSelectVisitor enter(SqlPlainSelect select, SqlNode parent) {
			NestedScope subScope = NestedScope.create(select, scope);
			select.setScope(subScope);
			return new ScopedVisitor(subScope);
		}


		@Override
		public void visit(SqlSelectAllColumns allColumns, SqlNode parent) {
			allColumns.setColumns(scope.localColumns());
		}

		@Override
		public void visit(SqlSelectAllColumnsInTable columnsInTable, SqlNode parent) {
			String tableAlias = columnsInTable.tableAlias();
			columnsInTable.setColumns(scope.columnsInTable(tableAlias));
		}
	}
}
