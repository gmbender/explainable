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

import com.github.explainable.sql.ast.expression.SqlExpression;
import com.github.explainable.sql.ast.select.SqlFrom;
import com.github.explainable.sql.ast.select.SqlSelectItem;
import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.google.common.base.Objects;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;

import javax.annotation.Nullable;

final class MasterConverter {
	@Nullable
	private ExpressionConverter expressionConverter;

	@Nullable
	private FromItemConverter fromItemConverter;

	@Nullable
	private ItemsListConverter itemsListConverter;

	@Nullable
	private SelectBodyConverter selectBodyConverter;

	@Nullable
	private SelectItemConverter selectItemConverter;

	MasterConverter() {
		this.expressionConverter = null;
		this.fromItemConverter = null;
		this.itemsListConverter = null;
		this.selectBodyConverter = null;
		this.selectItemConverter = null;
	}

	SqlExpression convert(Expression expression) {
		if (expressionConverter == null) {
			expressionConverter = new ExpressionConverter(this);
		}
		return expressionConverter.convert(expression);
	}

	SqlFrom convert(FromItem fromItem) {
		if (fromItemConverter == null) {
			fromItemConverter = new FromItemConverter(this);
		}
		return fromItemConverter.convert(fromItem);
	}

	SqlExpression convert(ItemsList itemsList) {
		if (itemsListConverter == null) {
			itemsListConverter = new ItemsListConverter(this);
		}
		return itemsListConverter.convert(itemsList);
	}

	SqlSelectStmt convert(SelectBody selectBody) {
		if (selectBodyConverter == null) {
			selectBodyConverter = new SelectBodyConverter(this);
		}
		return selectBodyConverter.convert(selectBody);
	}

	SqlSelectItem convert(SelectItem selectItems) {
		if (selectItemConverter == null) {
			selectItemConverter = new SelectItemConverter(this);
		}
		return selectItemConverter.convert(selectItems);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("expression", expressionConverter)
				.add("fromItem", fromItemConverter)
				.add("itemsList", itemsListConverter)
				.add("selectBody", selectBodyConverter)
				.add("selectItem", selectItemConverter)
				.toString();
	}
}
