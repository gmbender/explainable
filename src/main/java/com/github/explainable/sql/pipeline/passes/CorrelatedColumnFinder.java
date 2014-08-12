package com.github.explainable.sql.pipeline.passes;

import com.github.explainable.sql.ast.AbstractVisitor;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.expression.SqlColumnReference;
import com.github.explainable.sql.ast.expression.SqlSubSelect;
import com.github.explainable.sql.ast.select.SqlPlainSelect;
import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.github.explainable.sql.ast.select.SqlSelectVisitor;
import com.github.explainable.sql.pipeline.DependsOn;
import com.github.explainable.sql.pipeline.TransformationPass;
import com.github.explainable.sql.table.Column;
import com.github.explainable.sql.table.NestedScope;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Pass that finds <i>correlated</i> columns that are defined in one scope but are referenced in a
 * different scope. For instance, in the query
 * <pre>
 *     {@code SELECT sid FROM Sailors S WHERE EXISTS
 *          (SELECT 1 FROM Reserves R WHERE s.sid = R.sid)},
 * </pre>
 * the column {@code S.sid} is correlated because it's defined in the outer query but referenced by
 * the inner query. For each {@link SqlSubSelect}, we make a list of the columns that are defined in
 * the current scope and are referenced by the sub-select.
 */
@DependsOn(ColumnResolver.class)
final class CorrelatedColumnFinder implements TransformationPass {
	CorrelatedColumnFinder() {
	}

	@Override
	public void execute(SqlSelectStmt select) {
		select.accept(new BaseColumnVisitor(), null);
	}

	private static class BaseColumnVisitor extends AbstractVisitor {
		private final Map<NestedScope, SqlSubSelect> scopeToSubSelect;

		private BaseColumnVisitor() {
			scopeToSubSelect = Maps.newHashMap();
		}

		@Override
		public SqlSelectVisitor enter(SqlPlainSelect select, SqlNode parent) {
			return new CorrelatedColumnVisitor(select.getScope(), scopeToSubSelect);
		}
	}

	private static class CorrelatedColumnVisitor extends AbstractVisitor {
		private final NestedScope scope;

		private final Map<NestedScope, SqlSubSelect> scopeToSubSelect;

		private CorrelatedColumnVisitor(
				NestedScope scope,
				Map<NestedScope, SqlSubSelect> scopeToSubSelect) {
			this.scope = scope;
			this.scopeToSubSelect = scopeToSubSelect;
		}

		private void registerColumn(Column column) {
			SqlSubSelect subSelect = scopeToSubSelect.get(column.scope());
			if (subSelect != null) {
				subSelect.addCorrelatedColumn(column);
			}
		}

		@Override
		public void visit(SqlColumnReference reference, SqlNode parent) {
			registerColumn(reference.getColumn());
		}

		@Override
		public SqlSelectVisitor enter(SqlSubSelect subSelect, SqlNode parent) {
			scopeToSubSelect.put(scope, subSelect);
			return this;
		}

		@Override
		public void leave(SqlSubSelect subSelect, SqlNode parent) {
			scopeToSubSelect.remove(scope);
		}

		@Override
		public SqlSelectVisitor enter(SqlPlainSelect select, SqlNode parent) {
			return new CorrelatedColumnVisitor(select.getScope(), scopeToSubSelect);
		}
	}
}
