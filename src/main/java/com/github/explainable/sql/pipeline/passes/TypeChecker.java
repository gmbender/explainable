package com.github.explainable.sql.pipeline.passes;

import com.github.explainable.sql.ast.AbstractVisitor;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.github.explainable.sql.pipeline.DependsOn;
import com.github.explainable.sql.pipeline.TransformationPass;

/**
 * Pass that type-checks the AST for a SQL query.
 */
@DependsOn({AggTypeChecker.class, ColumnResolver.class})
final class TypeChecker implements TransformationPass {
	@Override
	public void execute(SqlSelectStmt select) {
		select.accept(new TypeCheckerVisitor(), null);
	}

	private static class TypeCheckerVisitor extends AbstractVisitor {
		@Override
		protected void defaultLeave(SqlNode node, SqlNode parent) {
			node.typeCheck();
		}

		@Override
		protected void defaultVisit(SqlNode node, SqlNode parent) {
			node.typeCheck();
		}
	}
}
