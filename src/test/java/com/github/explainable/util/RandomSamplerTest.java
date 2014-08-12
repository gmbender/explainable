package com.github.explainable.util;

import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 11/28/13 Time: 10:57 AM To change this template
 * use File | Settings | File Templates.
 */
public class RandomSamplerTest {
	private static final long SEED = 565723144L;

	private Random random = null;

	@Before
	public void setUp() {
		random = new Random(SEED);
	}

	@After
	public void tearDown() {
		random = null;
	}

	@Test(expected = NullPointerException.class)
	public void testSample_nullPopulation() {
		RandomSampler.create(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSample_sampleSizeIsTooLarge() {
		List<String> population = ImmutableList.of("one", "two", "three", "four", "five");
		RandomSampler.create(random).sample(population, 100);
	}

	@Test
	public void testSample() throws Exception {
		RandomSampler sampler = RandomSampler.create(random);

		List<Integer> population = ImmutableList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		final int sampleSize = 4;

		// placed[i][j] is true only if the ith element of the population ends up in position j
		// in one of the iterations below.
		boolean placed[][] = new boolean[sampleSize][population.size()];

		// Run the test many times and ensure that each element of the population appears in
		// each slot of the output sample at least once.
		for (int iteration = 0; iteration < 100; iteration++) {
			List<Integer> sample = sampler.sample(population, sampleSize);
			for (int i = 0; i < sample.size(); i++) {
				placed[i][sample.get(i)] = true;
			}
		}

		// For a population size of 10, a sample size of 4, and 100 iterations, a conservative
		// upper bound on the probability of failure is p = 40 * (.9 ^ 100), or about 0.1%
		for (int i = 0; i < placed.length; i++) {
			for (int j = 0; j < placed[i].length; j++) {
				assertTrue(placed[i][j]);
			}
		}
	}
}
