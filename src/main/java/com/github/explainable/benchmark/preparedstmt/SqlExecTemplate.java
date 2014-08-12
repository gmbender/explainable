package com.github.explainable.benchmark.preparedstmt;

import com.github.explainable.corelang.Term;
import com.github.explainable.util.RandomSampler;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by gbender on 12/5/13.
 */
final class SqlExecTemplate {
	private final String statementName;

	private final ImmutableList<ImmutableList<Term>> argValues;

	SqlExecTemplate(String statementName, List<ImmutableList<Term>> argValues) {
		this.statementName = Preconditions.checkNotNull(statementName);
		this.argValues = ImmutableList.copyOf((argValues));
	}

	SqlExec next(RandomSampler sampler) {
		List<Term> args = Lists.newArrayListWithCapacity(argValues.size());
		for (List<Term> candidates : argValues) {
			args.add(sampler.choice(candidates));
		}

		return SqlExec.create(statementName, args);
	}

	static Builder builder(String statementName) {
		return new Builder(statementName);
	}

	static final class Builder {
		private final String statementName;

		private final List<ImmutableList<Term>> argValues;

		private Builder(String statementName) {
			this.statementName = Preconditions.checkNotNull(statementName);
			this.argValues = Lists.newArrayList();
		}

		Builder addArg(List<Term> values) {
			argValues.add(ImmutableList.copyOf(values));
			return this;
		}

		SqlExecTemplate build() {
			return new SqlExecTemplate(statementName, argValues);
		}
	}
}
