package com.github.explainable.sql.converter;

import com.github.explainable.sql.ast.expression.SqlColumnReference;
import com.github.explainable.sql.ast.expression.SqlExpression;
import com.github.explainable.sql.ast.select.SqlSelectAllColumns;
import com.github.explainable.sql.ast.select.SqlSelectAllColumnsInTable;
import com.github.explainable.sql.ast.select.SqlSelectColumn;
import com.github.explainable.sql.ast.select.SqlSelectItem;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

import javax.annotation.Nullable;

import static com.github.explainable.sql.converter.ConverterUtils.checkUnsupportedFeature;

final class SelectItemConverter implements SelectItemVisitor {
	private final MasterConverter master;

	@Nullable
	private SqlSelectItem result;

	SelectItemConverter(MasterConverter master) {
		this.master = master;
		this.result = null;
	}

	@Nullable
	public SqlSelectItem convert(SelectItem selectItem) {
		// This silly dance is necessary to ensure that none of the accept(...) methods forget to
		// set the "result" variable. The code would be so much cleaner if accept(...) had a non-void
		// return type. Sigh.
		result = null;
		selectItem.accept(this);
		Preconditions.checkNotNull(result);
		SqlSelectItem realResult = result;
		result = null;
		return realResult;
	}

	@Override
	public void visit(AllColumns allColumns) {
		result = new SqlSelectAllColumns();
	}

	@Override
	public void visit(AllTableColumns allTableColumns) {
		checkUnsupportedFeature(allTableColumns.getTable().getAlias(), "SELECT item table alias");
		checkUnsupportedFeature(allTableColumns.getTable().getPivot(), "SELECT item table pivot");
		checkUnsupportedFeature(allTableColumns.getTable().getSchemaName(), "Schema Name");

		result = new SqlSelectAllColumnsInTable(allTableColumns.getTable().getName());
	}

	@Override
	public void visit(SelectExpressionItem item) {
		SqlExpression child = master.convert(item.getExpression());
		String alias = item.getAlias();

		if (alias == null && child instanceof SqlColumnReference) {
			alias = ((SqlColumnReference) child).columnName();
		}

		result = new SqlSelectColumn(child, alias);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("result", result)
				.toString();
	}
}
