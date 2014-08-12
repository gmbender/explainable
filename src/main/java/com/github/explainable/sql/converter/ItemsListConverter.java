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
