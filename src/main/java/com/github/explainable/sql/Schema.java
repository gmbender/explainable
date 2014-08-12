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

package com.github.explainable.sql;

import com.github.explainable.sql.table.TypedRelation;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public final class Schema {
	private final ImmutableList<TypedRelation> relations;

	private Schema(List<? extends TypedRelation> relations) {
		this.relations = ImmutableList.copyOf(relations);
	}

	public static Schema of(TypedRelation... relations) {
		return new Schema(Arrays.asList(relations));
	}

	public static Schema create(List<? extends TypedRelation> relations) {
		return new Schema(relations);
	}

	@Nullable
	public TypedRelation findRelation(String tableName) {
		Preconditions.checkNotNull(tableName);

		for (TypedRelation relation : relations) {
			if (relation.name().equals(tableName)) {
				return relation;
			}
		}

		return null;
	}

	public ImmutableList<TypedRelation> relations() {
		return relations;
	}

	@Override
	public String toString() {
		return Joiner.on(System.getProperty("line.separator")).join(relations);
	}
}
