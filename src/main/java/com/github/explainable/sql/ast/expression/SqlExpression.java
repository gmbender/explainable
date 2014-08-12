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
