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
