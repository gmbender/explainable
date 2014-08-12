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

import com.github.explainable.benchmark.FBFlatSchema;
import com.github.explainable.benchmark.SecurityViewReader;
import com.github.explainable.corelang.Term;
import com.github.explainable.corelang.Terms;
import com.github.explainable.corelang.View;
import com.github.explainable.labeler.policy.Policy;
import com.github.explainable.labeler.policy.PolicyLabeler;
import com.github.explainable.sql.pipeline.passes.ViewExtractionPipeline;
import com.github.explainable.util.RandomSampler;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.Select;

import java.io.File;
import java.io.StringReader;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Created by gbender on 12/6/13.
 */
public final class PrepStmtBenchmark2 implements Runnable {
	private enum BenchmarkStage {
		GENERATE,
		PARSE,
		COMPILE,
		LABEL,
		EXPLAIN
	}

	private static final ImmutableList<Term> CONSTANTS = ImmutableList.<Term>of(
			Terms.constant(4L),
			Terms.constant(4L),
			Terms.constant(4L),
			Terms.constant(4L),
			Terms.constant(4L),
			Terms.constant(4L),
			Terms.constant(12345L),
			Terms.constant("open"),
			Terms.constant("closed"),
			Terms.constant("everyone"),
			Terms.constant("friends"),
			Terms.constant("friends-of-friends"),
			Terms.constant("photo"),
			Terms.constant("checkin"),
			Terms.constant("video"),
			Terms.constant("status"));

	public static final File PREPARED_STATEMENT_FILE = new File("data/fb-prepared-statements.sql");

	public static final File SECURITY_VIEW_FILE = new File("data/fb-security-views.sql");

	public static final int MIN_THREAD_COUNT = 1;

	public static final int MAX_THREAD_COUNT = 8;

	private static final int BENCHMARK_TRIALS = 1000000;

	private static final int TRIAL_COUNT = 5;

	private static final ImmutableList<BenchmarkStage> ACTIVE_STAGES
			= ImmutableList.copyOf(BenchmarkStage.values());

	private final ImmutableList<View> securityViews;

	private final ImmutableMultimap<String, View> preparedViews;

	private final SqlExecGenerator execGenerator;

	private final CountDownLatch signal;

	private final int iterations;

	private final BenchmarkStage lastStage;

	private PrepStmtBenchmark2(
			List<View> securityViews,
			Multimap<String, View> preparedViews,
			SqlExecGenerator execGenerator,
			CountDownLatch signal,
			int iterations,
			BenchmarkStage lastStage) {
		Preconditions.checkArgument(iterations > 0);

		this.securityViews = ImmutableList.copyOf(securityViews);
		this.preparedViews = ImmutableMultimap.copyOf(preparedViews);
		this.execGenerator = Preconditions.checkNotNull(execGenerator);
		this.signal = Preconditions.checkNotNull(signal);
		this.iterations = iterations;
		this.lastStage = Preconditions.checkNotNull(lastStage);
	}

	@Override
	public void run() {
		try {
			RandomSampler sampler = RandomSampler.createAndSeed();
			PolicyLabeler labeler = PolicyLabeler.create(securityViews);
			Set<View> granted = ImmutableSet.copyOf(
					RandomSampler.createAndSeed().sample(securityViews, securityViews.size() / 2));

			for (int iterNum = 0; iterNum < iterations; iterNum++) {
				String nextExec = execGenerator.next(sampler).toString();

				if (lastStage.compareTo(BenchmarkStage.PARSE) < 0) {
					continue;
				}
				SqlExec parsedExec = ExecStmtParser.create(nextExec).parse();

				if (lastStage.compareTo(BenchmarkStage.COMPILE) < 0) {
					continue;
				}
				List<View> extractedViews = Lists.newArrayList();
				ExecSubstitutionMap subst = ExecSubstitutionMap.create(parsedExec.arguments());
				for (View view : preparedViews.get(parsedExec.statementName())) {
					extractedViews.add(view.apply(subst));
				}

				if (lastStage.compareTo(BenchmarkStage.LABEL) < 0) {
					continue;
				}
				Policy policy = labeler.label(extractedViews).simplify();

				if (lastStage.compareTo(BenchmarkStage.EXPLAIN) < 0) {
					continue;
				}
				if (policy.evaluate(granted)) {
					policy.whySo(granted);
				} else {
					policy.whyNot(granted);
				}
			}

			signal.countDown();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) throws Exception {
		List<View> preparedStmts
				= SecurityViewReader.readViews(PREPARED_STATEMENT_FILE, FBFlatSchema.SCHEMA);

		List<String> sqlPreparedStmts
				= SecurityViewReader.readSqlViews(PREPARED_STATEMENT_FILE, FBFlatSchema.SCHEMA);

		List<View> securityViews
				= SecurityViewReader.readViews(SECURITY_VIEW_FILE, FBFlatSchema.SCHEMA);

		System.out.format("# Name: %s%n", PrepStmtBenchmark2.class.getName());
		System.out.format("# Date: %s%n", new Date());
		System.out.format("# Host: %s%n", InetAddress.getLocalHost().getHostName());

		SqlExecGenerator.Builder gen = SqlExecGenerator.builder();
		ImmutableMultimap.Builder<String, View> preparedViewsBuilder = ImmutableMultimap.builder();

		CCJSqlParserManager parser = new CCJSqlParserManager();
		ViewExtractionPipeline extractor = ViewExtractionPipeline.create(FBFlatSchema.SCHEMA);

		for (int i = 0; i < preparedStmts.size(); i++) {
			String viewName = "V" + (i + 1);

			String stmt = sqlPreparedStmts.get(i);
			SqlExecTemplate.Builder builder = SqlExecTemplate.builder(viewName);
			for (int argIndex = 1; stmt.contains("\'$" + argIndex + "\'"); argIndex++) {
				builder.addArg(CONSTANTS);
			}

			gen.add(builder.build());

			Select parsedStmt = (Select) parser.parse(new StringReader(stmt));
			List<View> extractedViews = extractor.execute(parsedStmt);
			preparedViewsBuilder.putAll(viewName, extractedViews);
		}

		ImmutableMultimap<String, View> preparedViews = preparedViewsBuilder.build();
		SqlExecGenerator execGenerator = gen.build();

		for (BenchmarkStage lastStage : ACTIVE_STAGES) {
			System.out.format("%s = [%n", lastStage);
			for (int threadCount = MIN_THREAD_COUNT;
					threadCount <= MAX_THREAD_COUNT;
					threadCount++) {
				System.out.print("\t[ ");
				System.out.flush();

				for (int trialNum = 0; trialNum <= TRIAL_COUNT; trialNum++) {
					CountDownLatch doneSignal = new CountDownLatch(threadCount);
					long startTimeMillis = System.currentTimeMillis();

					for (int i = 0; i < threadCount; i++) {
						new Thread(
								new PrepStmtBenchmark2(
										securityViews,
										preparedViews,
										execGenerator,
										doneSignal,
										BENCHMARK_TRIALS / threadCount,
										lastStage)).start();
					}

					doneSignal.await();
					long endTimeMillis = System.currentTimeMillis();

					if (trialNum > 0) {
						System.out.format("%.2f", .001 * (endTimeMillis - startTimeMillis));
						if (trialNum < TRIAL_COUNT) {
							System.out.print(", ");
						}
						System.out.flush();
					}
				}
				System.out.format(" ], # %d thread(s)%n", threadCount);
			}
			System.out.println("]");
		}
	}
}
