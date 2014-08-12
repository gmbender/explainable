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

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.set;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for {@link Conjunction}.
 */
public class ConjunctionTest {
	@Test
	public void testFindHomomorphism_dist_dist() {
		Relation relation = RelationImpl.create("R", ImmutableList.of("X"));

		Atom atom1 = Atom.asSetAtom(relation, dist());
		Atom atom2 = Atom.asSetAtom(relation, dist());

		Conjunction singleAtom = Conjunction.create(ImmutableList.of(atom1));
		Conjunction twoAtom = Conjunction.create(ImmutableList.of(atom1, atom2));

		assertNotNull(singleAtom.findHomomorphism(twoAtom));
		assertNull(twoAtom.findHomomorphism(singleAtom));
	}

	@Test
	public void testFindHomomorphism_dist_set() {
		Relation relation = RelationImpl.create("R", ImmutableList.of("X"));

		Atom atom1 = Atom.asSetAtom(relation, dist());
		Atom atom2 = Atom.asSetAtom(relation, set());

		Conjunction singleAtom = Conjunction.create(ImmutableList.of(atom1));
		Conjunction twoAtom = Conjunction.create(ImmutableList.of(atom1, atom2));

		assertNotNull(singleAtom.findHomomorphism(twoAtom));
		assertNotNull(twoAtom.findHomomorphism(singleAtom));
	}

	@Test
	public void testFindHomomorphism_set_set() {
		Relation relation = RelationImpl.create("R", ImmutableList.of("X"));

		Atom atom1 = Atom.asSetAtom(relation, set());
		Atom atom2 = Atom.asSetAtom(relation, set());

		Conjunction singleAtom = Conjunction.create(ImmutableList.of(atom1));
		Conjunction twoAtom = Conjunction.create(ImmutableList.of(atom1, atom2));

		assertNotNull(singleAtom.findHomomorphism(twoAtom));
		assertNotNull(twoAtom.findHomomorphism(singleAtom));
	}
}
