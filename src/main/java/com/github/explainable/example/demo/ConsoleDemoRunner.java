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

import com.github.explainable.sql.SqlException;
import com.google.common.base.Preconditions;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.TokenMgrError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Class that provides a simple API for running disclosure labeling demos interactively in a
 * console.
 */
public final class ConsoleDemoRunner implements DemoRunner {
	private final BufferedReader inputReader;

	private final Demo demo;

	private boolean shouldExit;

	private ConsoleDemoRunner(BufferedReader inputReader, Demo demo) {
		this.inputReader = Preconditions.checkNotNull(inputReader);
		this.demo = Preconditions.checkNotNull(demo);
		this.shouldExit = false;
	}

	public static ConsoleDemoRunner create(Demo demo) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		return new ConsoleDemoRunner(reader, demo);
	}

	private void printHeader(String header, boolean sectionIsEmpty) {
		if (sectionIsEmpty) {
			System.out.println(header + ".");
		} else {
			System.out.println(header + ":");
		}
	}

	private void runOnce() throws IOException, JSQLParserException {
		printHeader("Enter SQL Query", false);
		String sql = inputReader.readLine();
		System.out.println();

		if (sql == null || sql.equalsIgnoreCase("quit") || sql.equalsIgnoreCase("exit")) {
			System.out.println("Goodbye");
			shouldExit = true;
		} else if (sql.equalsIgnoreCase("help")) {
			demo.showHelpMessage(this);
		} else {
			if (!sql.isEmpty()) {
				demo.handleQuery(sql, this);
			}
		}
	}

	@Override
	public void printEmptySection(String header) {
		printHeader(header, true);
		System.out.println();
	}

	@Override
	public void printSection(String header, List<?> values) {
		printHeader(header, false);

		if (values.isEmpty()) {
			System.out.println("Empty.");
		} else {
			for (Object value : values) {
				String valueString = value.toString();
				if (valueString.isEmpty()) {
					System.out.println();
				} else {
					System.out.println("* " + valueString);
				}
			}
		}

		System.out.println();
	}

	@Override
	public void load() {
		demo.showHelpMessage(this);

		while (!shouldExit) {
			try {
				runOnce();
			} catch (IOException e) {
				e.printStackTrace(System.out);
				shouldExit = true;
			} catch (SqlException e) {
				e.printStackTrace(System.out);
				System.out.println();
				demo.reset();
			} catch (JSQLParserException e) {
				if (e.getCause() != null) {
					System.out.println(e.getCause());
					System.out.println();
				} else if (e.getMessage() != null) {
					System.out.println(e.getMessage());
					System.out.println();
				} else {
					e.printStackTrace(System.out);
				}
				demo.reset();
			} catch (TokenMgrError e) {
				e.printStackTrace(System.out);
				System.out.println();
				demo.reset();
			}
		}
	}
}
