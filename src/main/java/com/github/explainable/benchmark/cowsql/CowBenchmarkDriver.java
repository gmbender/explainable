package com.github.explainable.benchmark.cowsql;

import com.github.explainable.benchmark.SecurityViewReader;
import com.github.explainable.corelang.View;
import com.github.explainable.labeler.policy.Policy;
import com.github.explainable.labeler.policy.PolicyLabeler;
import com.github.explainable.sql.pipeline.passes.ViewExtractionPipeline;
import com.github.explainable.util.RandomSampler;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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
 * Driver that produces random SQL queries by chaining together views stored in a file on disk.
 */
public final class CowBenchmarkDriver implements Runnable {
	private enum BenchmarkStage {
		GENERATE,
		PARSE,
		COMPILE,
		LABEL,
		EXPLAIN
	}

	public static final File TEMPLATE_FILE = new File("data/cow-templates.sql");

	public static final File SECURITY_VIEW_FILE = new File("data/cow-security-views.sql");

	public static final int MIN_THREAD_COUNT = 1;

	public static final int MAX_THREAD_COUNT = 8;

	private static final int BENCHMARK_TRIALS = 1000000;

	private static final int TRIAL_COUNT = 5;

	private static final ImmutableList<BenchmarkStage> ACTIVE_STAGES
			= ImmutableList.copyOf(BenchmarkStage.values());

	private final ImmutableList<String> prototypeQueries;

	private final ImmutableList<View> securityViews;

	private final CountDownLatch signal;

	private final int iterations;

	private final BenchmarkStage lastStage;

	private CowBenchmarkDriver(
			List<String> prototypeQueries,
			List<View> securityViews,
			CountDownLatch signal,
			int iterations,
			BenchmarkStage lastStage) {
		Preconditions.checkArgument(iterations > 0);

		this.prototypeQueries = ImmutableList.copyOf(prototypeQueries);
		this.securityViews = ImmutableList.copyOf(securityViews);
		this.signal = Preconditions.checkNotNull(signal);
		this.iterations = iterations;
		this.lastStage = Preconditions.checkNotNull(lastStage);
	}

	@Override
	public void run() {
		try {
			RandomSampler sampler = RandomSampler.createAndSeed();
			CCJSqlParserManager parser = new CCJSqlParserManager();
			ViewExtractionPipeline extractor = ViewExtractionPipeline.create(CowSqlSchema.SCHEMA);
			PolicyLabeler labeler = PolicyLabeler.create(securityViews);
			Set<View> granted = ImmutableSet.copyOf(
					RandomSampler.createAndSeed().sample(securityViews, securityViews.size() / 2));

			for (int iterNum = 0; iterNum < iterations; iterNum++) {
				String nextQuery = sampler.choice(prototypeQueries);

				if (lastStage.compareTo(BenchmarkStage.PARSE) < 0) {
					continue;
				}
				Select parsedQuery = (Select) parser.parse(new StringReader(nextQuery));

				if (lastStage.compareTo(BenchmarkStage.COMPILE) < 0) {
					continue;
				}
				List<View> extractedViews = extractor.execute(parsedQuery);

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
		List<String> prototypeQueries
				= SecurityViewReader.readSqlViews(TEMPLATE_FILE, CowSqlSchema.SCHEMA);

		List<View> securityViews
				= SecurityViewReader.readViews(SECURITY_VIEW_FILE, CowSqlSchema.SCHEMA);

		System.out.format("# Name: %s%n", CowBenchmarkDriver.class.getName());
		System.out.format("# Date: %s%n", new Date());
		System.out.format("# Host: %s%n", InetAddress.getLocalHost().getHostName());

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
								new CowBenchmarkDriver(
										prototypeQueries,
										securityViews,
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
