#!/usr/bin/python
# Must be run from test/edu/cornell/disclosure/sql/

import re

files = [
	'AggregateTest.java',
	'GroupByTest.java',
	'KnownBugsTest.java',
	'NestedTest.java',
	'OuterJoinTest.java',
	'SetOperationTest.java',
	'SimpleTest.java',
	'TemporaryTableTest.java'
]

for filename in files:
	print '-----', filename, '-----'
	print
	with open(filename, 'r') as handle:
		data = handle.read()

    # Extract all SQL queries
	for match in re.findall(r'sql = "([^;]+)";', data, re.MULTILINE):
	    # Handle multi-line strings
		cleaned = re.sub(r'\\n\"\s+\+\s+"', ' ', match, re.MULTILINE)

		# Remove all double spaces
		respaced = re.sub(r'\s+', ' ', cleaned);

		print respaced + ';'
		print
