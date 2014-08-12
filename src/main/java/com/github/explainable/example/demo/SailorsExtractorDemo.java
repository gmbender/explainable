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

package com.github.explainable.example.demo;

import com.github.explainable.corelang.View;
import com.github.explainable.sql.Schema;
import com.github.explainable.sql.pipeline.passes.ViewExtractionPipeline;
import com.github.explainable.sql.table.TypedRelation;
import com.github.explainable.sql.table.TypedRelationImpl;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.Select;

import java.io.StringReader;
import java.util.List;

import static com.github.explainable.sql.type.TypeSystem.numeric;
import static com.github.explainable.sql.type.TypeSystem.string;

/**
 * Demo of the SQL-to-atom extraction machinery using the Sailors-and-Boats database schema from the
 * Cow Book.
 */
public final class SailorsExtractorDemo implements Demo {
	private static final TypedRelation SAILORS = TypedRelationImpl.builder().setName("Sailors")
			.addColumn("sid", numeric())
			.addColumn("sname", string())
			.addColumn("rating", numeric())
			.addColumn("age", numeric())
			.build();

	private static final TypedRelation BOATS = TypedRelationImpl.builder().setName("Boats")
			.addColumn("bid", numeric())
			.addColumn("bname", string())
			.addColumn("color", string())
			.build();

	private static final TypedRelation RESERVES = TypedRelationImpl.builder().setName("Reserves")
			.addColumn("sid", numeric())
			.addColumn("bid", numeric())
			.addColumn("day", string())
			.build();

	private static final Schema SCHEMA = Schema.of(SAILORS, BOATS, RESERVES);

	private CCJSqlParserManager parserManager;

	private ViewExtractionPipeline extractionPipeline;

	private SailorsExtractorDemo() {
		this.parserManager = new CCJSqlParserManager();
		this.extractionPipeline = ViewExtractionPipeline.create(SCHEMA);
	}

	@Override
	public void showHelpMessage(DemoRunner runner) {
		runner.printSection("Schema", SCHEMA.relations());
	}

	@Override
	public void handleQuery(String sql, DemoRunner runner) throws JSQLParserException {
		Select select = (Select) parserManager.parse(new StringReader(sql));
		List<View> views = extractionPipeline.execute(select);

		runner.printSection("Extracted Atoms", views);
	}

	@Override
	public void reset() {
		parserManager = new CCJSqlParserManager();
		extractionPipeline = ViewExtractionPipeline.create(SCHEMA);
	}

	public static void main(String[] args) {
		SwingDemoRunner.create(new SailorsExtractorDemo()).load();
	}
}
