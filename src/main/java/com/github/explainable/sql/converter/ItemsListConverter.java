package com.github.explainable.sql.converter;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.ast.expression.SqlExpression;
import com.github.explainable.sql.ast.expression.SqlList;
import com.github.explainable.sql.ast.expression.SqlSubSelect;
import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.statement.select.SubSelect;

import javax.annotation.Nullable;
import java.util.List;

import static com.github.explainable.sql.converter.ConverterUtils.checkUnsupportedFeature;

final class ItemsListConverter implements ItemsListVisitor {
	private final MasterConverter master;

	@Nullable
	private SqlExpression result;

	ItemsListConverter(MasterConverter master) {
		this.master = master;
		this.result = null;
	}

	@Nullable
	SqlExpression convert(ItemsList itemsList) {
		// This silly dance is necessary to ensure that none of the accept(...) methods forget to
		// set the "result" variable. The code would be so much cleaner if accept(...) had a non-void
		// return type. Sigh.
		result = null;
		itemsList.accept(this);
		Preconditions.checkNotNull(result);
		SqlExpression realResult = result;
		result = null;
		return realResult;
	}

	@Override
	public void visit(SubSelect subSelect) {
		checkUnsupportedFeature(subSelect.getPivot(), "PIVOT");
		checkUnsupportedFeature(subSelect.getAlias(), "ALIAS");

		SqlSelectStmt subSelectBody = master.convert(subSelect.getSelectBody());

		result = new SqlSubSelect(subSelectBody);
	}

	@Override
	public void visit(ExpressionList expressionList) {
		List<SqlExpression> convertedExpressions = Lists.newArrayList();

		for (Expression expression : expressionList.getExpressions()) {
			convertedExpressions.add(master.convert(expression));
		}

		result = new SqlList(convertedExpressions);
	}

	@Override
	public void visit(MultiExpressionList multiExpressionList) {
		throw new SqlException("Unsupported Feature: MultiExpressionList");
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("result", result)
				.toString();
	}
}
