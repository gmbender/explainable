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
