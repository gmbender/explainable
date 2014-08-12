package com.github.explainable.benchmark.preparedstmt;

import com.github.explainable.corelang.Term;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Created by gbender on 12/5/13.
 */
final class SqlExec {
	private final String procedureName;

	private final ImmutableList<Term> arguments;

	private SqlExec(String procedureName, List<? extends Term> arguments) {
		this.procedureName = procedureName;
		this.arguments = ImmutableList.copyOf(arguments);
	}

	public static SqlExec create(String statementName, List<? extends Term> arguments) {
		return new SqlExec(statementName, arguments);
	}

	String statementName() {
		return procedureName;
	}

	ImmutableList<Term> arguments() {
		return arguments;
	}

	@Override
	public int hashCode() {
		return procedureName.hashCode() + 17 * arguments.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SqlExec)) {
			return false;
		}
		SqlExec other = (SqlExec) obj;
		return procedureName.equals(other.procedureName)
				&& arguments.equals(other.arguments);
	}

	@Override
	public String toString() {
		return "EXECUTE " + statementName() + "(" + Joiner.on(", ").join(arguments) + ");";
	}
}
