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

import org.junit.Test;

import static com.github.explainable.corelang.Terms.constant;
import static com.github.explainable.corelang.Terms.dist;
import static com.github.explainable.corelang.Terms.multiset;
import static com.github.explainable.corelang.Terms.set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link Term}.
 */
public class TermTest {
	@Test
	public void testUnifyWith_differentTypes() throws Exception {
		Constant constant = constant("hello");
		DistVariable distVar = dist();
		MultisetVariable multisetVar = multiset();
		SetVariable setVar = set();

		assertEquals(constant, constant.unifyWith(constant));
		assertEquals(constant, constant.unifyWith(distVar));
		assertEquals(constant, constant.unifyWith(multisetVar));
		assertEquals(constant, constant.unifyWith(setVar));

		assertEquals(constant, distVar.unifyWith(constant));
		assertEquals(distVar, distVar.unifyWith(distVar));
		assertEquals(distVar, distVar.unifyWith(multisetVar));
		assertEquals(distVar, distVar.unifyWith(setVar));

		assertEquals(constant, multisetVar.unifyWith(constant));
		assertEquals(distVar, multisetVar.unifyWith(distVar));
		assertEquals(multisetVar, multisetVar.unifyWith(multisetVar));
		assertEquals(multisetVar, multisetVar.unifyWith(setVar));

		assertEquals(constant, setVar.unifyWith(constant));
		assertEquals(distVar, setVar.unifyWith(distVar));
		assertEquals(multisetVar, setVar.unifyWith(multisetVar));
		assertEquals(setVar, setVar.unifyWith(setVar));
	}

	@Test
	public void testUnifyWith_equalConstant() throws Exception {
		Constant constant1 = constant("hello");
		Constant constant2 = constant("hello");
		assertEquals(constant1, constant1.unifyWith(constant2));
		assertEquals(constant2, constant1.unifyWith(constant2));
	}

	@Test
	public void testUnifyWith_notEqualConstant() throws Exception {
		Constant constant1 = constant("hello");
		Constant constant2 = constant("goodbye");
		assertNull(constant1.unifyWith(constant2));
	}

	@Test
	public void testUnifyWith_distVariable() throws Exception {
		DistVariable distVar1 = dist();
		DistVariable distVar2 = dist();
		assertTrue(distVar1.unifyWith(distVar2) instanceof DistVariable);
	}

	@Test
	public void testUnifyWith_multisetVariable() throws Exception {
		MultisetVariable multisetVar1 = multiset();
		MultisetVariable multisetVar2 = multiset();
		assertTrue(multisetVar1.unifyWith(multisetVar2) instanceof MultisetVariable);
	}

	@Test
	public void testUnifyWith_setVariable() throws Exception {
		SetVariable setVar1 = set();
		SetVariable setVar2 = set();
		assertTrue(setVar1.unifyWith(setVar2) instanceof SetVariable);
	}
}
