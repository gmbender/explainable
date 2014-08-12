package com.github.explainable.util;

/**
 * Interface that can see the distinct vertices in a traversal over a {@link LDGraph}.
 */
public interface LDVertexVisitor<V, L> {
	void visit(LDVertex<V, L> vertex);
}
