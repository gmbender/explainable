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

package com.github.explainable.corelang;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Class that represents a database relation in our core query language.
 */
public final class RelationImpl implements Relation {
	private final String tableName;

	private final ImmutableList<String> fields;

	private RelationImpl(String tableName, List<String> fields) {
		this.tableName = Preconditions.checkNotNull(tableName);
		this.fields = ImmutableList.copyOf(fields);
	}


	public static RelationImpl create(String tableName, List<String> fields) {
		return new RelationImpl(tableName, fields);
	}

	@Override
	public String name() {
		return tableName;
	}

	@Override
	public int arity() {
		return fields.size();
	}

	@Override
	public ImmutableList<String> columnNames() {
		return fields;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RelationImpl)) {
			return false;
		}

		RelationImpl other = (RelationImpl) o;
		return tableName.equals(other.tableName) && fields.equals(other.fields);
	}

	@Override
	public int hashCode() {
		return tableName.hashCode() + 37 * fields.hashCode();
	}

	@Override
	public String toString() {
		return tableName + "(" + Joiner.on(", ").join(fields) + ")";
	}
}
