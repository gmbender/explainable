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
