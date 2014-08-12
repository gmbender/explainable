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
import com.github.explainable.sql.ast.expression.SqlBinaryExpression;
import com.github.explainable.sql.ast.expression.SqlBinaryExpression.BinaryOperator;
import com.github.explainable.sql.ast.expression.SqlExists;
import com.github.explainable.sql.ast.expression.SqlIn;
import com.github.explainable.sql.ast.expression.SqlSubSelect;
import com.github.explainable.sql.ast.select.*;
import com.github.explainable.sql.ast.select.SqlFromJoin.SqlJoinKind;
import com.github.explainable.sql.constraint.EqualityConstraint;
import com.github.explainable.sql.pipeline.DependsOn;
import com.github.explainable.sql.pipeline.TransformationPass;
import com.github.explainable.sql.table.BaseTable;
import com.github.explainable.sql.type.RowCount;
import com.github.explainable.util.LDGraph;
import com.github.explainable.util.LDVertex;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

/**
 * Pass that initializes the condition graph for a given query. The condition graph captures join
 * dependencies between different parts of the query. It contains a vertex for each {@link
 * SqlPlainSelect}, {@link SqlFrom} subclass, and {@link BaseTable} in the AST. If {@code T1} and
 * {@code T2} are {@link BaseTable}s then there should be a path from {@code T1} to {@code T2} in
 * the condition graph if and only if {@code T2} appears as a condition atom in the view associated
 * with {@code T1}. For instance, for the query
 *
 * <pre>
 * {@code SELECT 1 FROM Sailors S LEFT JOIN Reserves R ON (S.sid = R.sid)}
 * </pre>
 *
 * should have a path from {@code R} to {@code S} but not from {@code S} to {@code R}.
 * Initialization is done in this pass, while equality constraints are added later on by {@link
 * CondGraphAnnotator}.
 */
@DependsOn({ColumnResolver.class, AggTypeChecker.class})
final class CondGraphInitializer implements TransformationPass {
	@Override
	public void execute(SqlSelectStmt select) {
		LDGraph<Object, EqualityConstraint> graph = LDGraph.create();
		select.accept(new DefaultVisitor(graph, null), null);
	}

	private static final class DefaultVisitor extends AbstractVisitor {
		private final LDGraph<Object, EqualityConstraint> graph;

		@Nullable
		private final SqlPlainSelect outerSelect;

		DefaultVisitor(LDGraph<Object, EqualityConstraint> graph, SqlPlainSelect outerSelect) {
			this.graph = Preconditions.checkNotNull(graph);
			this.outerSelect = outerSelect;
		}

		@Override
		public void leave(SqlFromClause from, SqlNode parent) {
			SqlPlainSelect select = (SqlPlainSelect) parent;

			LDVertex<Object, EqualityConstraint> fromVertex = from.from().getConditionVertex();
			LDVertex<Object, EqualityConstraint> selectVertex = select.getConditionVertex();

			fromVertex.addEdge(selectVertex);
			selectVertex.addEdge(fromVertex);
		}

		@Override
		public void visit(SqlFromBaseTable from, SqlNode parent) {
			BaseTable baseTable = from.getBaseTable();
			LDVertex<Object, EqualityConstraint> baseTableVertex = graph.addVertex(baseTable);
			baseTable.setConditionVertex(baseTableVertex);

			LDVertex<Object, EqualityConstraint> fromVertex = graph.addVertex(from);
			from.setConditionVertex(fromVertex);

			baseTableVertex.addEdge(fromVertex);
			fromVertex.addEdge(baseTableVertex);
		}

		@Override
		public AbstractVisitor enter(SqlFromSubSelect from, SqlNode parent) {
			from.setConditionVertex(graph.addVertex(from));
			return this;
		}

		@Override
		public AbstractVisitor enter(SqlPlainSelect select, SqlNode parent) {
			select.setConditionVertex(graph.addVertex(select));
			return new DefaultVisitor(graph, select);
		}

		@Override
		public void leave(SqlPlainSelect select, SqlNode parent) {
			if (outerSelect != null) {
				select.getConditionVertex().addEdge(outerSelect.getConditionVertex());
			}
		}

		@Override
		public AbstractVisitor enter(SqlFromJoin join, SqlNode parent) {
			join.setConditionVertex(graph.addVertex(join));
			return this;
		}

		@Override
		public void leave(SqlFromJoin join, SqlNode parent) {
			LDVertex<Object, EqualityConstraint> condVertex = join.getConditionVertex();

			LDVertex<Object, EqualityConstraint> leftVertex = join.left().getConditionVertex();
			leftVertex.addEdge(condVertex);
			if (join.kind() == SqlJoinKind.INNER || join.kind() == SqlJoinKind.LEFT_OUTER) {
				condVertex.addEdge(leftVertex);
			}

			LDVertex<Object, EqualityConstraint> rightVertex = join.right().getConditionVertex();
			rightVertex.addEdge(condVertex);
			if (join.kind() == SqlJoinKind.INNER || join.kind() == SqlJoinKind.RIGHT_OUTER) {
				condVertex.addEdge(rightVertex);
			}
		}

		@Override
		public AbstractVisitor enter(SqlOnClause on, SqlNode parent) {
			SqlFromJoin join = (SqlFromJoin) parent;
			return new PredicateVisitor(graph, join.getConditionVertex(), outerSelect);
		}

		@Override
		public AbstractVisitor enter(SqlWhereClause where, SqlNode parent) {
			if (outerSelect == null) {
				throw new IllegalStateException();
			}
			return new PredicateVisitor(graph, outerSelect.getConditionVertex(), outerSelect);
		}

		@Override
		public AbstractVisitor enter(SqlHavingClause having, SqlNode parent) {
			if (outerSelect == null) {
				throw new IllegalStateException();
			}
			return new PredicateVisitor(graph, outerSelect.getConditionVertex(), outerSelect);
		}
	}

	private static class PredicateVisitor extends AbstractVisitor {
		private final LDGraph<Object, EqualityConstraint> graph;

		private final LDVertex<Object, EqualityConstraint> startVertex;

		private final SqlPlainSelect outerSelect;

		private PredicateVisitor(
				LDGraph<Object, EqualityConstraint> graph,
				LDVertex<Object, EqualityConstraint> startVertex,
				SqlPlainSelect outerSelect) {
			this.graph = Preconditions.checkNotNull(graph);
			this.startVertex = Preconditions.checkNotNull(startVertex);
			this.outerSelect = Preconditions.checkNotNull(outerSelect);
		}

		@Override
		protected AbstractVisitor defaultEnter(SqlNode node, SqlNode parent) {
			return new DefaultVisitor(graph, outerSelect);
		}

		@Override
		public AbstractVisitor enter(SqlBinaryExpression expr, SqlNode parent) {
			if (expr.operator() == BinaryOperator.AND) {
				return this;
			} else {
				return defaultEnter(expr, parent);
			}
		}

		@Override
		public AbstractVisitor enter(SqlExists exists, SqlNode parent) {
			if (exists.isNot()) {
				return defaultEnter(exists, parent);
			} else {
				return this;
			}
		}

		@Override
		public AbstractVisitor enter(SqlIn in, SqlNode parent) {
			if (in.isNot()) {
				return defaultEnter(in, parent);
			} else {
				return this;
			}
		}

		@Override
		public void leave(SqlSubSelect subSelect, SqlNode parent) {
			SqlSelectStmt innerSelect = subSelect.select();
			if (innerSelect instanceof SqlPlainSelect) {
				SqlPlainSelect plainSelect = (SqlPlainSelect) innerSelect;
				if (plainSelect.getType().rowCount() == RowCount.UNLIMITED_ROWS) {
					startVertex.addEdge(plainSelect.getConditionVertex());
				}
			}
		}
	}
}
