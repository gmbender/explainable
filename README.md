Explainable Security for Relational Databases
===========

This source code accompanies the 2014 SIGMOD paper [Explainable Security for Relational Databases](http://www.cs.cornell.edu/~lucja/Publications/sigmod2014-explainable.pdf) by Gabriel Bender, Lucja Kot, and Johannes Gehrke. The source code was developed as a proof-of-concept for an experimental model of database access control which is discussed in detail in that paper. We strongly encourage prospective developers to read that paper before looking at our source code in depth.

**WARNING**: The source code was developed as a proof of concept and is **not** ready for use in production environments. In particular, it should not be used in security-critical contexts. However, it is probably stable enough for use in research prototypes. 

Building the Project
-----------

To build the project, you'll need Maven 3 and Java 1.6 or later. After cloning this repository, you must run the following command from the project's root directory:
```
mvn package
```
Once the project has been built, you can play around with a demo application that was used in our SIGMOD 2014 talk by typing
```
java -jar target/explainable-0.1-SNAPSHOT.jar
```

SIGMOD Experiments
-----------
An electronic appendix for our SIGMOD paper that contains an extended experimental evaluation is available [here](http://www.cs.cornell.edu/~lucja/Publications/sigmod2014appendix.pdf). Below we include instructions for generating the data used in those experiments. All shell commands should be run from the project's root directory on Linux or Mac OS X.

1. To produce five random queries with the query generator we used for our experiments, execute the following command:
```
java -cp target/explainable-0.1-SNAPSHOT.jar com.github.explainable.example.demo.ConjunctionGeneratorDemo
```
The resulting queries will be different every time you run the command. Users who wish to use the query generator in their own experiments should start by looking at the source code of the associated Java file.

2. To reproduce the data used to generate Figure 2 of the appendix, execute the following command:
```
java -cp target/explainable-0.1-SNAPSHOT.jar com.github.explainable.benchmark.mockfb.FBBenchmarkDriver
```

3. To reproduce the data used to generate Figure 3 of the appendix, execute the following command:
```
java -cp target/explainable-0.1-SNAPSHOT.jar com.github.explainable.benchmark.preparedstmt.PrepStmtBenchmark2
```

4. To reproduce the data used to generate Figure 4 of the appendix, execute the following command:
```
java -cp target/explainable-0.1-SNAPSHOT.jar com.github.explainable.benchmark.cowsql.CowBenchmarkDriver
```
