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
