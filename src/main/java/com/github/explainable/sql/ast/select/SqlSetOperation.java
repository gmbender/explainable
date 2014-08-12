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
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.SchemaTableType;
import com.github.explainable.sql.type.Type;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

/**
 * Class representing a set operation such as {@code UNION} or {@code INTERSECT} that is applied to
 * the output of two SQL queries.
 */
public final class SqlSetOperation extends SqlSelectStmt {
	private final SqlSelectStmt left;

	private final SqlSelectStmt right;

	private final SqlSetOperationKind kind;

	public SqlSetOperation(SqlSelectStmt left, SqlSelectStmt right, SqlSetOperationKind kind) {
		this.left = Preconditions.checkNotNull(left);
		this.right = Preconditions.checkNotNull(right);
		this.kind = Preconditions.checkNotNull(kind);
	}

	private SchemaTableType resultType(SchemaTableType leftType, SchemaTableType rightType) {
		SchemaTableType schemaType = kind.resultType(leftType, rightType).coerceToSchemaTable();

		if (schemaType == null) {
			throw new SqlException("Must be schema table: " + schemaType);
		}

		return schemaType;
	}

	@Nullable
	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		return left.getAggType().commonSupertype(right.getAggType());
	}

	@Override
	protected SchemaTableType typeCheckImpl() {
		return resultType(left.getType(), right.getType());
	}

	@Override
	public ImmutableList<String> columnNames() {
		return left.columnNames();
	}

	@Override
	public void accept(SqlSelectVisitor visitor, SqlNode parent) {
		SqlSelectVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			left.accept(childVisitor, this);
			right.accept(childVisitor, this);
		}
		visitor.leave(this, parent);
	}

	@Override
	public String toString() {
		return left + " " + kind + " " + right;
	}

	public enum SqlSetOperationKind {
		EXCEPT {
			@Override
			Type resultType(SchemaTableType leftType, SchemaTableType rightType) {
				if (leftType.commonSupertype(rightType) == null) {
					throw new SqlException(
							"No common supertype: " + leftType + " and " + rightType);
				}
				return leftType;
			}
		},
		INTERSECT {
			@Override
			Type resultType(SchemaTableType leftType, SchemaTableType rightType) {
				Type resultType = leftType.unifyWith(rightType);
				if (resultType == null) {
					throw new SqlException("Can't unify: " + leftType + " and " + rightType);
				}
				return resultType;
			}
		},
		UNION {
			@Override
			Type resultType(SchemaTableType leftType, SchemaTableType rightType) {
				Type resultType = leftType.commonSupertype(rightType);
				if (resultType == null) {
					throw new SqlException(
							"No common supertype: " + leftType + " and " + rightType);
				}
				return resultType;
			}
		};

		abstract Type resultType(SchemaTableType leftType, SchemaTableType rightType);
	}
}
