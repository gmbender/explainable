package com.github.explainable.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;

/**
 * Class that selects a random subset of the elements of a list uniformly at random.
 */
public final class RandomSampler {
	private final Random random;

	private RandomSampler(Random random) {
		this.random = Preconditions.checkNotNull(random);
	}

	public static RandomSampler createAndSeed() {
		return new RandomSampler(new Random());
	}

	public static RandomSampler create(Random random) {
		return new RandomSampler(random);
	}

	public Random random() {
		return random;
	}

	/**
	 * Obtain a uniform random sample of the elements in the population. The order of elements in the
	 * output list is also uniformly random.
	 *
	 * @param population the population from which a sample is to be drawn
	 * @param n the sample size
	 * @return a sample of size {@code n} drawn from {@code population} uniformly at random
	 */
	public <T> List<T> sample(List<T> population, int n) {
		Preconditions.checkNotNull(population);
		Preconditions.checkArgument(n <= population.size());

		boolean[] selected = new boolean[population.size()];

		List<T> result = Lists.newArrayListWithCapacity(n);
		for (int i = 0; i < n; i++) {
			int j = random.nextInt(population.size());
			while (selected[j]) {
				j = random.nextInt(population.size());
			}

			selected[j] = true;
			result.add(population.get(j));
		}

		return result;
	}

	/**
	 * Select a single item in the specified list uniformly at random.
	 */
	public <T> T choice(List<T> population) {
		return population.get(random.nextInt(population.size()));
	}

	/**
	 * Select a number in the range [0, n] by clamping a Laplace distribution with expected value
	 * lambda.
	 */
	public int nextPoisson(double lambda, int n) {
		Preconditions.checkArgument(lambda > 0);
		Preconditions.checkArgument(n > 0);

		// density: Pr[k] = pow(lambda, k) * exp(-lambda) / factorial(k)
		double density = Math.exp(-lambda);
		double cumulativeDensity = 0.0;
		double target = random.nextDouble();

		for (int k = 0; k < n; k++) {
			cumulativeDensity += density;
			if (cumulativeDensity >= target) {
				return k;
			}

			density *= lambda;
			density /= (k + 1);
		}

		return n;
	}
}
