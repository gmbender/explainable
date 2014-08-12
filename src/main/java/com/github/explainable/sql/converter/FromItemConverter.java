package com.github.explainable.sql.converter;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.ast.select.SqlFrom;
import com.github.explainable.sql.ast.select.SqlFromBaseTable;
import com.github.explainable.sql.ast.select.SqlFromSubSelect;
import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.ValuesList;

import javax.annotation.Nullable;

import static com.github.explainable.sql.converter.ConverterUtils.checkUnsupportedFeature;

final class FromItemConverter implements FromItemVisitor {
	private final MasterConverter master;

	@Nullable
	private SqlFrom result;

	FromItemConverter(MasterConverter master) {
		this.master = Preconditions.checkNotNull(master);
		this.result = null;
	}

	@Nullable
	SqlFrom convert(FromItem fromItem) {
		// This silly dance is necessary to ensure that none of the accept(...) methods forget to
		// set the "result" variable. The code would be so much cleaner if accept(...) had a non-void
		// return type. Sigh.
		result = null;
		fromItem.accept(this);
		Preconditions.checkNotNull(result);
		SqlFrom realResult = result;
		result = null;
		return realResult;
	}

	@Override
	public void visit(Table table) {
		checkUnsupportedFeature(table.getSchemaName(), "Schema Name");
		checkUnsupportedFeature(table.getPivot(), "PIVOT");

		result = new SqlFromBaseTable(table.getName(), table.getAlias());
	}

	@Override
	public void visit(SubSelect subSelect) {
		checkUnsupportedFeature(subSelect.getPivot(), "SubSelect Pivot");

		SqlSelectStmt body = master.convert(subSelect.getSelectBody());
		String alias = subSelect.getAlias();

		if (alias == null) {
			throw new SqlException("SubSelect in FROM clause must have alias: " + subSelect);
		}

		result = new SqlFromSubSelect(body, alias);
	}

	@Override
	public void visit(SubJoin subJoin) {
		throw new SqlException("Unsupported Feature: SubJoin in FROM");
	}

	@Override
	public void visit(LateralSubSelect lateralSubSelect) {
		throw new SqlException("Unsupported Feature: LATERAL");
	}

	@Override
	public void visit(ValuesList valuesList) {
		throw new SqlException("FROM Values List");
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("result", result)
				.toString();
	}
}
