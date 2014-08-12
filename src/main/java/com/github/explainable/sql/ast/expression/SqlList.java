package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.PrimitiveType;
import com.github.explainable.sql.type.RowCount;
import com.github.explainable.sql.type.Type;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.Collection;

public final class SqlList extends SqlExpression {
	private final ImmutableList<SqlExpression> elements;

	public SqlList(@Nullable Collection<SqlExpression> elements) {
		Preconditions.checkArgument(elements != null && !elements.isEmpty());
		this.elements = ImmutableList.copyOf(elements);
	}

	@Override
	public void accept(SqlExpressionVisitor visitor, SqlNode parent) {
		SqlExpressionVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			for (SqlExpression expr : elements) {
				expr.accept(childVisitor, this);
			}
		}
		visitor.leave(this, parent);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		AggType resultType = null;
		for (SqlExpression expr : elements) {
			AggType exprType = expr.getAggType();

			if (resultType == null) {
				resultType = exprType;
			} else {
				resultType = resultType.commonSupertype(exprType);
				if (resultType == null) {
					throw new SqlException(
							"Aggregate/non-aggregate mismatch: " + resultType + " and " + exprType);
				}
			}
		}

		return resultType;
	}

	@Override
	protected Type typeCheckImpl() {
		// The null type is a subtype of all other primitive types.
		PrimitiveType resultType = TypeSystem.primitiveBottom();

		for (SqlExpression element : elements) {
			PrimitiveType elementType = element.getType().coerceToPrimitive();
			if (elementType == null) {
				throw new SqlException("Must be primitive: " + elementType);
			}

			resultType = elementType.commonSupertype(elementType);
		}

		RowCount rowCount = (elements.size() == 1) ? RowCount.SINGLE_ROW : RowCount.UNLIMITED_ROWS;

		return TypeSystem.schemaList(rowCount, resultType);
	}

	@Override
	public EqualityArg equalityArg() {
		if (elements.size() == 1) {
			return elements.get(0).equalityArg();
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		Joiner.on(", ").appendTo(builder, elements);
		builder.append(")");
		return builder.toString();
	}
}
