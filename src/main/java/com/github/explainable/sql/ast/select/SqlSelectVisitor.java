package com.github.explainable.sql.ast.select;

import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.ast.expression.SqlExpressionVisitor;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 10/15/13 Time: 9:40 AM To change this template
 * use File | Settings | File Templates.
 */
public interface SqlSelectVisitor {
	void visit(SqlFromBaseTable from, SqlNode parent);

	@Nullable
	SqlSelectVisitor enter(SqlFromSubSelect from, SqlNode parent);

	void leave(SqlFromSubSelect from, SqlNode parent);

	@Nullable
	SqlSelectVisitor enter(SqlPlainSelect select, SqlNode parent);

	void leave(SqlPlainSelect select, SqlNode parent);

	void visit(SqlSelectAllColumns allColumns, SqlNode parent);

	void visit(SqlSelectAllColumnsInTable columnsInTable, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlSelectColumn column, SqlNode parent);

	void leave(SqlSelectColumn column, SqlNode parent);

	@Nullable
	SqlSelectVisitor enter(SqlSetOperation operation, SqlNode parent);

	void leave(SqlSetOperation operation, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlWhereClause where, SqlNode parent);

	void leave(SqlWhereClause where, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlHavingClause having, SqlNode parent);

	void leave(SqlHavingClause having, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlGroupByClause groupBy, SqlNode parent);

	void leave(SqlGroupByClause groupBy, SqlNode parent);

	@Nullable
	SqlSelectVisitor enter(SqlFromClause from, SqlNode parent);

	void leave(SqlFromClause from, SqlNode parent);

	SqlSelectVisitor enter(SqlSelectItemsClause output, SqlNode parent);

	void leave(SqlSelectItemsClause output, SqlNode parent);

	@Nullable
	SqlExpressionVisitor enter(SqlOnClause on, SqlNode parent);

	void leave(SqlOnClause on, SqlNode parent);

	SqlSelectVisitor enter(SqlFromJoin join, SqlNode parent);

	void leave(SqlFromJoin join, SqlNode parent);
}
