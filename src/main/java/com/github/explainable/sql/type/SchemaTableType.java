package com.github.explainable.sql.type;

import com.google.common.collect.ImmutableList;

/**
 * Created with IntelliJ IDEA. User: gbender Date: 9/23/13 Time: 10:29 AM To change this template
 * use File | Settings | File Templates.
 */
public interface SchemaTableType extends TableType {
	int arity();

	ImmutableList<PrimitiveType> columnTypes();

	RowCount rowCount();
}
