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

package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.constraint.EqualityArg;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.type.Type;
import com.github.explainable.sql.type.TypeSystem;

public final class SqlBinaryExpression extends SqlExpression {
	private final BinaryOperator operator;

	private final SqlExpression left;

	private final SqlExpression right;

	public SqlBinaryExpression(SqlExpression left, SqlExpression right, BinaryOperator operator) {
		this.left = left;
		this.right = right;
		this.operator = operator;
	}

	@Override
	public void accept(SqlExpressionVisitor visitor, SqlNode parent) {
		SqlExpressionVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			left.accept(childVisitor, this);
			right.accept(childVisitor, this);
		}
		visitor.leave(this, parent);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		AggType resultType = left.getAggType().commonSupertype(right.getAggType());
		if (resultType == null) {
			throw new SqlException("Aggregate/non-aggregate mismatch: " + left + " and " + right);
		}

		return resultType;
	}

	@Override
	protected Type typeCheckImpl() {
		return operator.typeCheck(left, right);
	}

	@Override
	public EqualityArg equalityArg() {
		return null;
	}

	public SqlExpression left() {
		return left;
	}

	public SqlExpression right() {
		return right;
	}

	public BinaryOperator operator() {
		return operator;
	}

	@Override
	public String toString() {
		return "(" + left.toString() + " " + operator + " " + right.toString() + ")";
	}

	private enum BinaryOperatorType {
		ARITHMETIC_OPERATOR {
			@Override
			Type typeCheck(SqlExpression left, SqlExpression right) {
				if (left.getType().coerceToNumeric() == null) {
					throw new SqlException("Must be numeric: " + left);
				}
				if (right.getType().coerceToNumeric() == null) {
					throw new SqlException("Must be numeric: " + right);
				}
				return TypeSystem.numeric();
			}
		},
		NUMERIC_COMPARISON {
			@Override
			Type typeCheck(SqlExpression left, SqlExpression right) {
				if (left.getType().coerceToNumeric() == null) {
					throw new SqlException("Must be numeric: " + left);
				}
				if (right.getType().coerceToNumeric() == null) {
					throw new SqlException("Must be numeric: " + right);
				}
				return TypeSystem.bool();
			}
		},
		EQUALITY_COMPARISON {
			@Override
			Type typeCheck(SqlExpression left, SqlExpression right) {
				if (left.getType().coerceToPrimitive() == null) {
					throw new SqlException("Must be primitive: " + left);
				}
				if (right.getType().coerceToPrimitive() == null) {
					throw new SqlException("Must be primitive: " + right);
				}
				return TypeSystem.bool();
			}
		},
		LOGICAL_OPERATOR {
			@Override
			Type typeCheck(SqlExpression left, SqlExpression right) {
				if (left.getType().coerceToBool() == null) {
					throw new SqlException("Must be bool: " + left);
				}
				if (right.getType().coerceToBool() == null) {
					throw new SqlException("Must be bool: " + right);
				}
				return TypeSystem.bool();
			}
		};

		abstract Type typeCheck(SqlExpression left, SqlExpression right);
	}

	public enum BinaryOperator {
		ADDITION("+", BinaryOperatorType.ARITHMETIC_OPERATOR),
		SUBTRACTION("-", BinaryOperatorType.ARITHMETIC_OPERATOR),
		MULTIPLICATION("*", BinaryOperatorType.ARITHMETIC_OPERATOR),
		DIVISION("/", BinaryOperatorType.ARITHMETIC_OPERATOR),
		MODULO("%", BinaryOperatorType.ARITHMETIC_OPERATOR),
		BITWISE_AND("&", BinaryOperatorType.ARITHMETIC_OPERATOR),
		BITWISE_OR("|", BinaryOperatorType.ARITHMETIC_OPERATOR),
		BITWISE_XOR("^", BinaryOperatorType.ARITHMETIC_OPERATOR),
		GREATER_THAN(">", BinaryOperatorType.NUMERIC_COMPARISON),
		GREATER_THAN_EQUALS(">=", BinaryOperatorType.NUMERIC_COMPARISON),
		SMALLER_THAN("<", BinaryOperatorType.NUMERIC_COMPARISON),
		SMALLER_THAN_EQUALS("<=", BinaryOperatorType.NUMERIC_COMPARISON),
		EQUALS_TO("=", BinaryOperatorType.EQUALITY_COMPARISON),
		NOT_EQUALS_TO("<>", BinaryOperatorType.EQUALITY_COMPARISON),
		AND("AND", BinaryOperatorType.LOGICAL_OPERATOR),
		OR("OR", BinaryOperatorType.LOGICAL_OPERATOR);

		private final String symbol;

		private final BinaryOperatorType type;

		BinaryOperator(String symbol, BinaryOperatorType type) {
			this.symbol = symbol;
			this.type = type;
		}

		public Type typeCheck(SqlExpression left, SqlExpression right) {
			return type.typeCheck(left, right);
		}

		@Override
		public String toString() {
			return symbol;
		}
	}
}
