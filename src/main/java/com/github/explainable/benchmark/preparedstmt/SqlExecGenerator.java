package com.github.explainable.benchmark.preparedstmt;

import com.github.explainable.util.RandomSampler;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by gbender on 12/5/13.
 */
final class SqlExecGenerator {
	private final ImmutableList<SqlExecTemplate> templates;

	private SqlExecGenerator(List<SqlExecTemplate> templates) {
		this.templates = ImmutableList.copyOf(templates);
	}

	static final class Builder {
		private List<SqlExecTemplate> templates;

		private Builder() {
			this.templates = Lists.newArrayList();
		}

		Builder add(SqlExecTemplate template) {
			templates.add(Preconditions.checkNotNull(template));
			return this;
		}

		SqlExecGenerator build() {
			Preconditions.checkState(!templates.isEmpty(), "List of templates must be non-empty");
			return new SqlExecGenerator(templates);
		}
	}

	static Builder builder() {
		return new Builder();
	}

	SqlExec next(RandomSampler sampler) {
		return sampler.choice(templates).next(sampler);
	}
}
