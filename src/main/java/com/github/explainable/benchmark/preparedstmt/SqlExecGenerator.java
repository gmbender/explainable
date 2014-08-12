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
