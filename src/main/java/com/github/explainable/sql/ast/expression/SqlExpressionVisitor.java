package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.select.SqlSelectVisitor;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/17/13 Time: 10:00 AM To change this template
 * use File | Settings | File Templates.
 */
public interface SqlExpressionVisitor {
	@Nullable
	SqlExpressionVisitor enter(SqlBinaryExpression expr, SqlNode parent);

	void leave(SqlBinaryExpression expr, SqlNode parent);

	void visit(SqlColumnReference reference, SqlNode parent);

	void visit(SqlCountAll count, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlExists exists, SqlNode parent);

	void leave(SqlExists exists, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlIn in, SqlNode parent);

	void leave(SqlIn in, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlIsNull isNull, SqlNode parent);

	void leave(SqlIsNull isNull, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlList list, SqlNode parent);

	void leave(SqlList list, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlNegate negate, SqlNode parent);

	void leave(SqlNegate negate, SqlNode parent);

	void visit(SqlNull sqlNull, SqlNode parent);

	void visit(SqlNumericConstant constant, SqlNode parent);

	void visit(SqlStringConstant constant, SqlNode parent);

	@Nullable
	SqlSelectVisitor enter(SqlSubSelect subSelect, SqlNode parent);

	void leave(SqlSubSelect subSelect, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlTableComparison comparison, SqlNode parent);

	void leave(SqlTableComparison comparison, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlUnaryAggregate aggregate, SqlNode parent);

	void leave(SqlUnaryAggregate aggregate, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlLike like, SqlNode parent);

	void leave(SqlLike like, SqlNode parent);
}
