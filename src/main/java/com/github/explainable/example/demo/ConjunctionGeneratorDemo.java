package com.github.explainable.example.demo;

import com.github.explainable.benchmark.ConjunctionGenerator;
import com.github.explainable.benchmark.ConjunctionToSql;
import com.github.explainable.benchmark.FBFlatSchema;
import com.github.explainable.benchmark.SecurityViewReader;
import com.github.explainable.corelang.Conjunction;
import com.github.explainable.corelang.View;
import net.sf.jsqlparser.JSQLParserException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Class designed to illustrate the use of {@link ConjunctionGenerator}.
 */
public final class ConjunctionGeneratorDemo {
	// The name of the file that we should read query templates from.
	public static final String TEMPLATE_FILE_NAME = "data/fb-templates.sql";

	// The number of variables in the SELECT list will be drawn from a Poisson distribution with
	// parameter LAMBDA.
	public static final double LAMBDA = 3.0;

	public static void main(String[] args) throws IOException, JSQLParserException {
		// Read the list of templates that we should use when generating random queries.
		File templateFile = new File(TEMPLATE_FILE_NAME);

		List<View> templateViews
				= SecurityViewReader.readViews(templateFile, FBFlatSchema.SCHEMA);

		// Create an object that can generate random conjunctive queries using the templates.
		ConjunctionGenerator randConj = ConjunctionGenerator.createAndSeed(templateViews);

		for (int i = 0; i < 100; i++) {
			// Generate a new conjunctive query.
			Conjunction nextConj = randConj.nextWithRandomDist(LAMBDA);

			// Convert the conjunctive query to a SQL query.
			String nextQuery = ConjunctionToSql.convert(nextConj);

			// Print out the SQL query.
			System.out.println(nextQuery);
		}
	}
}
