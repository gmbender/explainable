package com.github.explainable.sql.converter;

import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.google.common.base.Objects;
import net.sf.jsqlparser.statement.select.Select;

import static com.github.explainable.sql.converter.ConverterUtils.checkUnsupportedFeature;

/**
 * Class that converts a {@code JSqlParser} parse tree into our own internal representation. Our
 * representation has some extra fields that can be filled in by AST visitors.
 */
public final class SelectStatementConverter {
	private final MasterConverter master;

	private SelectStatementConverter() {
		this.master = new MasterConverter();
	}

	public static SelectStatementConverter create() {
		return new SelectStatementConverter();
	}

	public SqlSelectStmt convert(Select select) {
		checkUnsupportedFeature(select.getWithItemsList(), "WITH");
		return master.convert(select.getSelectBody());
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("master", master)
				.toString();
	}
}
