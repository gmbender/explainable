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

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.aggtype.AggTypeSystem;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.expression.SqlColumnReference;
import com.github.explainable.sql.ast.expression.SqlExpression;
import com.github.explainable.sql.constraint.EqualityConstraint;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.table.NestedScope;
import com.github.explainable.sql.type.RowCount;
import com.github.explainable.sql.type.SchemaTableType;
import com.github.explainable.sql.type.TypeSystem;
import com.github.explainable.util.LDVertex;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

public final class SqlPlainSelect extends SqlSelectStmt {
	private final SqlSelectItemsClause selectItems;

	@Nullable
	private final SqlFromClause from;

	@Nullable
	private final SqlWhereClause where;

	@Nullable
	private final SqlGroupByClause groupBy;

	@Nullable
	private final SqlHavingClause having;

	@Nullable
	private final Long limit;

	@Nullable
	private final Long offset;

	private final boolean distinct;

	@Nullable
	private NestedScope scope;

	@Nullable
	private LDVertex<Object, EqualityConstraint> conditionVertex;

	private SqlPlainSelect(
			SqlSelectItemsClause selectItems,
			@Nullable SqlFromClause from,
			@Nullable SqlWhereClause where,
			@Nullable SqlGroupByClause groupBy,
			@Nullable SqlHavingClause having,
			@Nullable Long limit,
			@Nullable Long offset,
			boolean distinct) {
		if (groupBy == null && having != null) {
			throw new SqlException("Can't have HAVING without GROUP BY");
		}

		this.selectItems = Preconditions.checkNotNull(selectItems);
		this.from = from;
		this.where = where;
		this.groupBy = groupBy;
		this.having = having;
		this.distinct = distinct;
		this.limit = limit;
		this.offset = offset;
		this.scope = null;
	}

	@Nullable
	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		if (where != null) {
			if (where.getAggType().toNonAggregate() == null) {
				throw new SqlException("Must be non-aggregate: " + where);
			}
		}

		if (having != null) {
			if (having.getAggType().toAggregate() == null) {
				throw new SqlException("Must be aggregate: " + having);
			}
		}

		AggType resultType = selectItems.getAggType();
		if (from == null && resultType.toNonAggregate() == null) {
			throw new SqlException("Can't have aggregation without a FROM clause: " + selectItems);
		}

		return resultType;
	}

	@Override
	protected SchemaTableType typeCheckImpl() {
		SchemaTableType outputTypes = selectItems.getType();

		if (where != null) {
			if (where.getType().coerceToBool() == null) {
				throw new SqlException("Must be bool: " + where);
			}
		}

		if (having != null) {
			if (having.getType().coerceToBool() == null) {
				throw new SqlException("Must be bool: " + having);
			}
		}

		return TypeSystem.schemaTable(rowCount(), outputTypes.columnTypes());
	}

	private RowCount rowCount() {
		RowCount rowCount = RowCount.UNLIMITED_ROWS;

		if (groupBy == null && getAggType().equals(AggTypeSystem.agg())) {
			// If there isn't a GROUP BY clause and there's aggregation operator like COUNT(*)
			// in the SELECT clause then the result will contain exactly one row. We check
			// resultType.equals(AggTypeSystem.aggregate() instead of checking whether
			// resultType.toAggregate() is null in order to rule out things like SELECT 1.
			rowCount = RowCount.SINGLE_ROW;
		}

		if (from == null && where == null && having == null) {
			// The query is something database independent, like (SELECT 1).
			rowCount = RowCount.SINGLE_ROW;
		}

		return rowCount;
	}

	@Override
	public ImmutableList<String> columnNames() {
		return selectItems.columnNames();
	}

	@Nullable
	public SqlFromClause from() {
		return from;
	}

	@Nullable
	public SqlGroupByClause groupBy() {
		return groupBy;
	}

	@Nullable
	public SqlHavingClause having() {
		return having;
	}

	public SqlSelectItemsClause selectItems() {
		return selectItems;
	}

	@Nullable
	public SqlWhereClause where() {
		return where;
	}

	@Nullable
	public Long limit() {
		return limit;
	}

	@Nullable
	public Long offset() {
		return offset;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public NestedScope getScope() {
		if (scope == null) {
			throw new IllegalStateException("Scope has not been set for expression: " + this);
		}

		return scope;
	}

	public void setScope(NestedScope scope) {
		this.scope = Preconditions.checkNotNull(scope);
	}

	@Override
	public void accept(SqlSelectVisitor visitor, SqlNode parent) {
		SqlSelectVisitor childVisitor = visitor.enter(this, parent);

		if (childVisitor != null) {
			if (from != null) {
				from.accept(childVisitor, this);
			}

			if (where != null) {
				where.accept(childVisitor, this);
			}

			if (groupBy != null) {
				groupBy.accept(childVisitor, this);
			}

			if (having != null) {
				having.accept(childVisitor, this);
			}

			selectItems.accept(childVisitor, this);
		}

		visitor.leave(this, parent);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ");

		if (distinct) {
			builder.append("DISTINCT ");
		}

		builder.append(selectItems);

		if (from != null) {
			builder.append(from);
		}

		if (where != null) {
			builder.append(where);
		}

		if (groupBy != null) {
			builder.append(groupBy);
		}

		if (having != null) {
			builder.append(" HAVING ").append(having);
		}

		if (limit != null) {
			builder.append(" LIMIT ").append(limit);
		}

		if (offset != null) {
			builder.append(" OFFSET ").append(offset);
		}

		return builder.toString();
	}

	public static Builder builder() {
		return new Builder();
	}

	public void setConditionVertex(LDVertex<Object, EqualityConstraint> conditionVertex) {
		this.conditionVertex = Preconditions.checkNotNull(conditionVertex);
	}

	public LDVertex<Object, EqualityConstraint> getConditionVertex() {
		if (conditionVertex == null) {
			throw new IllegalStateException();
		}
		return conditionVertex;
	}

	public static class Builder {
		private final List<SqlSelectItem> selectItems;

		private final List<SqlColumnReference> groupBy;

		@Nullable
		private SqlFromClause from;

		@Nullable
		private SqlWhereClause where;

		@Nullable
		private SqlHavingClause having;

		@Nullable
		private Long limit;

		@Nullable
		private Long offset;

		private boolean distinct;

		private Builder() {
			this.selectItems = Lists.newArrayList();
			this.from = null;
			this.where = null;
			this.groupBy = Lists.newArrayList();
			this.having = null;
			this.limit = null;
			this.offset = null;
			this.distinct = false;
		}

		public Builder addSelectItem(SqlSelectItem item) {
			selectItems.add(item);
			return this;
		}

		public Builder setFrom(SqlFrom from) {
			this.from = new SqlFromClause(from);
			return this;
		}

		public Builder setWhere(SqlExpression where) {
			this.where = new SqlWhereClause(where);
			return this;
		}

		public Builder addGroupBy(SqlColumnReference columnRef) {
			this.groupBy.add(columnRef);
			return this;
		}

		public Builder setHaving(SqlExpression having) {
			this.having = new SqlHavingClause(having);
			return this;
		}

		public Builder setDistinct(boolean distinct) {
			this.distinct = distinct;
			return this;
		}

		public Builder setLimit(long limit) {
			this.limit = limit;
			return this;
		}

		public Builder setOffset(long offset) {
			this.offset = offset;
			return this;
		}

		public SqlPlainSelect build() {
			return new SqlPlainSelect(
					new SqlSelectItemsClause(selectItems),
					from,
					where,
					groupBy.isEmpty() ? null : new SqlGroupByClause(groupBy),
					having,
					limit,
					offset,
					distinct);
		}
	}
}
