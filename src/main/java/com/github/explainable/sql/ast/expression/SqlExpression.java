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

package com.github.explainable.sql.ast.expression;

import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.constraint.EqualityArg;

public abstract class SqlExpression extends SqlNode {

	/**
	 * If the current object has no children then this method should invoke {@code
	 * visitor.accept(this)}. If the current object has one or more children then this method should
	 * invoke {@code visitor.enter(this)}. If the return type is a non-null visitor {@code childViz}
	 * then it should call {@code accept(childViz)} on each of the node's children. Finally, it should
	 * invoke {@code visitor.leave(this)}. Children should be visited in their logical evaluation
	 * order.
	 */
	public abstract void accept(SqlExpressionVisitor visitor, SqlNode parent);

	/**
	 * Get a term representing the value of the expression rooted at the current node.
	 *
	 * @return a {@link EqualityArg}, or {@code null} if no conversion is possible
	 * @throws IllegalStateException if some of the fields that are required to compute the constraint
	 * have not been set
	 */
	public abstract EqualityArg equalityArg();
}
