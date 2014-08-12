package com.github.explainable.sql.pipeline.passes;

import com.github.explainable.corelang.Atom;
import com.github.explainable.corelang.Relation;
import com.github.explainable.corelang.Term;
import com.github.explainable.corelang.Terms;
import com.github.explainable.corelang.View;
import com.github.explainable.sql.ast.AbstractVisitor;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.select.SqlFromBaseTable;
import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.github.explainable.sql.constraint.BaseColumnArg;
import com.github.explainable.sql.constraint.ConstantArg;
import com.github.explainable.sql.constraint.EqualityConstraint;
import com.github.explainable.sql.constraint.EqualityConstraintMatcher;
import com.github.explainable.sql.pipeline.DependsOn;
import com.github.explainable.sql.pipeline.OutputPass;
import com.github.explainable.sql.table.BaseColumn;
import com.github.explainable.sql.table.BaseTable;
import com.github.explainable.util.LDVertex;
import com.github.explainable.util.LDVertexVisitor;
import com.github.explainable.util.UnionFindNode;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.explainable.corelang.Atom.createMultisetAtom;
import static com.github.explainable.corelang.View.convert;

/**
 * Output pass that takes the AST of a SQL query and computes a list of atoms with the property that
 * the answers to all the atoms in the list together contain enough information to uniquely
 * determine the answer to the original query.
 */
@DependsOn({TermInitializer.class, CondGraphInitializer.class, CondGraphAnnotator.class})
final class ViewExtractor implements OutputPass<ImmutableList<View>> {
	@Override
	public ImmutableList<View> execute(SqlSelectStmt select) {
		AstVisitor visitor = new AstVisitor();
		select.accept(visitor, null);
		return visitor.views();
	}

	private static final class AstVisitor extends AbstractVisitor {
		private final List<View> views;

		private int count;

		private AstVisitor() {
			this.views = Lists.newArrayList();
			this.count = 0;
		}

		private ImmutableList<View> views() {
			return ImmutableList.copyOf(views);
		}

		@Override
		public void visit(SqlFromBaseTable from, SqlNode parent) {
			BaseTable table = from.getBaseTable();
			count++;

			Map<BaseColumn, UnionFindNode<Term>> columnMap = Maps.newHashMap();
			table.getConditionVertex().visitReachableVertices(new TermMapInitializer(columnMap));
			table.getConditionVertex().visitReachableVertices(new VertexVisitor(columnMap));

			AtomExtractor extractor = new AtomExtractor(columnMap, "Q" + count);
			table.getConditionVertex().visitReachableVertices(extractor);
			views.add(extractor.view());
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this)
					.add("views", views)
					.toString();
		}
	}

	private static class VertexVisitor implements LDVertexVisitor<Object, EqualityConstraint> {
		private final EdgeMatcher matcher;

		VertexVisitor(Map<BaseColumn, UnionFindNode<Term>> columnMap) {
			this.matcher = new EdgeMatcher(columnMap);
		}

		@Override
		public void visit(LDVertex<Object, EqualityConstraint> vertex) {
			for (EqualityConstraint constraint : vertex.annotations()) {
				constraint.match(matcher);
			}
		}
	}

	private static final class EdgeMatcher implements EqualityConstraintMatcher {
		private final Map<BaseColumn, UnionFindNode<Term>> columnMap;

		private EdgeMatcher(Map<BaseColumn, UnionFindNode<Term>> columnMap) {
			this.columnMap = Preconditions.checkNotNull(columnMap);
		}

		@Override
		public void match(ConstantArg left, ConstantArg right) {
		}

		@Override
		public void match(ConstantArg left, BaseColumnArg right) {
			UnionFindNode<Term> rightUnionFind = columnMap.get(right.column());

			if (rightUnionFind != null) {
				Term leftTerm = Terms.constant(left.value());
				Term rightTerm = rightUnionFind.get();
				Term unifier = leftTerm.unifyWith(rightTerm);

				if (unifier != null) {
					rightUnionFind.set(unifier);
				}
			}
		}

		@Override
		public void match(BaseColumnArg left, ConstantArg right) {
			UnionFindNode<Term> leftUnionFind = columnMap.get(left.column());

			if (leftUnionFind != null) {
				Term leftTerm = Terms.constant(right.value());
				Term rightTerm = leftUnionFind.get();
				Term unifier = leftTerm.unifyWith(rightTerm);

				if (unifier != null) {
					leftUnionFind.set(unifier);
				}
			}
		}

		@Override
		public void match(BaseColumnArg left, BaseColumnArg right) {
			UnionFindNode<Term> leftUnionFind = columnMap.get(left.column());
			UnionFindNode<Term> rightUnionFind = columnMap.get(right.column());

			if (leftUnionFind != null && rightUnionFind != null) {
				Term leftTerm = leftUnionFind.get();
				Term rightTerm = rightUnionFind.get();
				Term unifier = leftTerm.unifyWith(rightTerm);

				if (unifier != null) {
					leftUnionFind.set(unifier);
					rightUnionFind.set(unifier);
					leftUnionFind.mergeWith(rightUnionFind);
				}
			}
		}
	}

	private static final class TermMapInitializer
			implements LDVertexVisitor<Object, EqualityConstraint> {
		private final Map<BaseColumn, UnionFindNode<Term>> columnMap;

		private final Set<BaseTable> tableSet;

		TermMapInitializer(Map<BaseColumn, UnionFindNode<Term>> columnMap) {
			this.columnMap = Preconditions.checkNotNull(columnMap);
			tableSet = Sets.newHashSet();
		}

		private void registerTable(BaseTable table) {
			if (!tableSet.contains(table)) {
				tableSet.add(table);

				for (BaseColumn newColumn : table.columns()) {
					columnMap.put(newColumn, UnionFindNode.create(newColumn.getTerm()));
				}
			}
		}

		@Override
		public void visit(LDVertex<Object, EqualityConstraint> vertex) {
			Object key = vertex.key();
			if (key instanceof BaseTable) {
				registerTable((BaseTable) key);
			}
		}
	}

	private static final class AtomExtractor
			implements LDVertexVisitor<Object, EqualityConstraint> {
		private final Map<BaseColumn, UnionFindNode<Term>> columnMap;

		private Atom body;

		private final List<Atom> conditions;

		private final String name;

		AtomExtractor(Map<BaseColumn, UnionFindNode<Term>> columnMap, String name) {
			this.columnMap = Preconditions.checkNotNull(columnMap);
			this.name = Preconditions.checkNotNull(name);
			this.body = null;
			this.conditions = Lists.newArrayList();
		}

		View view() {
			Preconditions.checkState(body != null);
			return convert(name, body, conditions);
		}

		private Atom makeAtom(BaseTable table) {
			Relation relation = table.relation();

			List<Term> terms = Lists.newArrayListWithCapacity(relation.arity());
			for (BaseColumn column : table.columns()) {
				terms.add(columnMap.get(column).get());
			}

			return createMultisetAtom(relation, terms);
		}

		@Override
		public void visit(LDVertex<Object, EqualityConstraint> vertex) {
			if (vertex.key() instanceof BaseTable) {
				if (body == null) {
					body = makeAtom((BaseTable) vertex.key());
				} else {
					conditions.add(makeAtom((BaseTable) vertex.key()));
				}
			}
		}
	}
}
