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
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.TokenMgrError;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Simple graphical shell for running disclosure labeling demos.
 */
public final class SwingDemoRunner extends JFrame implements DemoRunner, ActionListener {
	private static final int HEADER_FONT_SIZE = 24;

	private static final int BODY_FONT_SIZE = 24;

	private final Demo demo;

	private JTextPane outputPane;

	private JTextField inputField;

	private JMenuItem showSchemaMenuItem;

	private StyledDocument doc;

	private Style headerStyle;

	private Style bodyStyle;

	private SwingDemoRunner(Demo demo) {
		super("Disclosure Labeling Demo");
		this.demo = demo;
		this.outputPane = null;
		this.inputField = null;
		this.showSchemaMenuItem = null;
		this.doc = null;
		this.headerStyle = null;
		this.bodyStyle = null;
	}

	public static SwingDemoRunner create(Demo demo) {
		return new SwingDemoRunner(demo);
	}

	private void initFonts() {
		StyleContext sc = new StyleContext();

		headerStyle = sc.addStyle("HeaderStyle", null);
		StyleConstants.setFontFamily(headerStyle, "monospaced");
		StyleConstants.setForeground(headerStyle, Color.RED);
		StyleConstants.setFontSize(headerStyle, HEADER_FONT_SIZE);

		bodyStyle = sc.addStyle("BodyStyle", null);
		StyleConstants.setFontFamily(bodyStyle, "monospaced");
		StyleConstants.setForeground(bodyStyle, Color.BLACK);
		StyleConstants.setFontSize(bodyStyle, BODY_FONT_SIZE);
	}

	private void initContentPane() {
		getContentPane().setLayout(new BorderLayout());

		outputPane = new JTextPane();
		outputPane.setEditable(false);
		outputPane.setMargin(new Insets(10, 10, 10, 10));
		getContentPane().add(new JScrollPane(outputPane), BorderLayout.CENTER);

		DefaultCaret outputPaneCaret = (DefaultCaret) outputPane.getCaret();
		outputPaneCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

		inputField = new JTextField();
		inputField.setFont(new Font("monospaced", Font.PLAIN, BODY_FONT_SIZE));
		inputField.addActionListener(this);
		getContentPane().add(inputField, BorderLayout.SOUTH);

		doc = outputPane.getStyledDocument();
	}

	private void initMenus() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);

		showSchemaMenuItem = new JMenuItem("Show Help Message");
		showSchemaMenuItem.addActionListener(this);
		helpMenu.add(showSchemaMenuItem);
	}

	private void initWindowAndShow() {
		// Set up the window's behavior and make it visible.
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
		setVisible(true);

		inputField.requestFocus();
	}

	private SwingDemoRunner init() {
		initFonts();
		initContentPane();
		initMenus();
		initWindowAndShow();
		return this;
	}

	private void appendOutputLine(String line, Style style) {
		try {
			doc.insertString(doc.getLength(), String.format("%s%n", line), style);
		} catch (BadLocationException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public void printEmptySection(String header) {
		appendOutputLine(header + ".", headerStyle);
		appendOutputLine("", bodyStyle);
	}

	@Override
	public void printSection(String header, List<?> values) {
		appendOutputLine(header + ":", headerStyle);

		if (values.isEmpty()) {
			appendOutputLine(" \u2022 Nothing to see here, folks. Move along.", bodyStyle);
		} else {
			for (Object value : values) {
				String valueString = value.toString();

				if (valueString.isEmpty()) {
					appendOutputLine("", bodyStyle);
				} else {
					appendOutputLine(" \u2022 " + valueString, bodyStyle);
				}
			}
		}

		appendOutputLine("", bodyStyle);
	}

	@Override
	public void load() {
		init();
		demo.showHelpMessage(this);
	}

	private void handleQuery(String sql) {
		outputPane.setText("");
		printSection("Query", ImmutableList.of(sql));

		try {
			demo.handleQuery(sql, this);
		} catch (SqlException e) {
			demo.reset();
			printSection("Our query analysis pipeline rejected the query. Here's the error",
					ImmutableList.of(e.getMessage()));
		} catch (JSQLParserException e) {
			demo.reset();
			printSection("The SQL parser rejected the query. Here's the fine print",
					e.getCause() != null ? ImmutableList.of(e.getCause())
							: ImmutableList.copyOf(e.getStackTrace()));
		} catch (TokenMgrError e) {
			demo.reset();
			printSection("Wow. The SQL parser REALLY didn't like that. Here's the fine print",
					e.getMessage() != null ? ImmutableList.of(e.getMessage())
							: ImmutableList.of(e));
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == inputField) {
			String sql = inputField.getText();
			inputField.selectAll();
			if (!sql.isEmpty()) {
				handleQuery(sql);
			}
		} else if (event.getSource() == showSchemaMenuItem) {
			outputPane.setText("");
			demo.showHelpMessage(this);
		}
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("demo", demo)
				.toString();
	}
}
