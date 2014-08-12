package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.aggtype.AggTypeSystem;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.select.SqlPlainSelect;
import com.github.explainable.sql.ast.select.SqlSelectColumn;
import com.github.explainable.sql.ast.select.SqlSelectItem;
import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.github.explainable.sql.ast.select.SqlSelectVisitor;
import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.table.Column;
import com.github.explainable.sql.type.Type;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.Set;

public final class SqlSubSelect extends SqlExpression {
	private final SqlSelectStmt select;

	private final Set<Column> correlatedColumns; // Can be appended arbitrarily.

	public SqlSubSelect(SqlSelectStmt select) {
		this.select = Preconditions.checkNotNull(select);
		this.correlatedColumns = Sets.newHashSet();
	}

	@Override
	public void accept(SqlExpressionVisitor visitor, SqlNode parent) {
		SqlSelectVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			select.accept(childVisitor, this);
		}
		visitor.leave(this, parent);
	}

	@Nullable
	@Override
	public EqualityArg equalityArg() {
		if (select instanceof SqlPlainSelect) {
			ImmutableList<SqlSelectItem> items = ((SqlPlainSelect) select).selectItems().itemList();

			if (items.size() == 1) {
				SqlSelectItem item = items.get(0);

				if (item instanceof SqlSelectColumn) {
					return ((SqlSelectColumn) item).value().equalityArg();
				}
			}
		}

		return null;
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		AggType resultType = AggTypeSystem.aggOrNot();

		for (Column column : correlatedColumns) {
			resultType = resultType.commonSupertype(typeForColumn.getAggType(column));

			if (resultType == null) {
				throw new SqlException("Aggregate/Non-Aggregate mismatch for correlated columns: "
						+ correlatedColumns);
			}
		}

		return resultType;
	}

	@Override
	protected Type typeCheckImpl() {
		return select.getType();
	}

	public SqlSelectStmt select() {
		return select;
	}

	@Override
	public String toString() {
		return "(" + select + ")";
	}

	public void addCorrelatedColumn(Column column) {
		correlatedColumns.add(column);
	}

	public ImmutableSet<Column> getCorrelatedColumns() {
		return ImmutableSet.copyOf(correlatedColumns);
	}
}
