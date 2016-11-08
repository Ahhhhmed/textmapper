/**
 * Copyright 2002-2016 Evgeny Gryaznov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.textmapper.tool.compiler;

import org.junit.Test;

import static org.junit.Assert.*;

public class TMFieldTest {

	@Test
	public void mergeUnnamed() throws Exception {
		// Mergeable types.
		TMField f1 = new TMField("type1");
		TMField f2 = new TMField("type2");
		TMField f3 = new TMField("type3");

		TMField result = TMField.merge(f1, f2, f3);
		assertNotNull(result);
		assertEquals("?=(type1 | type2 | type3)", result.toString());
		assertFalse(result.hasExplicitName());

		result = result.withName("a");
		assertEquals("a=(type1 | type2 | type3)", result.toString());
		assertFalse(result.hasExplicitName());

		// If all names are equal, name hint is optional.
		f1 = f1.withName("type");
		f2 = f2.withName("type");
		f3 = f3.withName("type");

		result = TMField.merge(f1, f2, f3);
		assertNotNull(result);
		assertEquals("type=(type1 | type2 | type3)", result.toString());
		assertFalse(result.hasExplicitName());

		// Same field multiple times is fine.
		f2 = f1.withName("type");
		f3 = f1.withName("type");

		result = TMField.merge(f1, f2, f3);
		assertNotNull(result);
		assertEquals("type=type1", result.toString());
		assertFalse(result.hasExplicitName());

		// Name hint is respected.
		result = TMField.merge(f1, f2, f3).withName("abc");
		assertNotNull(result);
		assertEquals("abc=type1", result.toString());
		assertFalse(result.hasExplicitName());
	}

	@Test
	public void mergeNamed() throws Exception {
		// Types with explicit names.
		TMField f1 = new TMField("type1").withExplicitName("type", false);
		TMField f2 = new TMField("type2").withExplicitName("type", false);
		TMField f3 = new TMField("type3").withExplicitName("type", false);

		TMField result = TMField.merge(f1, f2, f3);
		assertNotNull(result);
		assertEquals("type=(type1 | type2 | type3)", result.toString());
		assertTrue(result.hasExplicitName());

		// Hint is ignored.
		result = TMField.merge(f1, f2, f3);
		assertNotNull(result);
		assertEquals("type=(type1 | type2 | type3)", result.toString());
		assertTrue(result.hasExplicitName());
	}

	@Test
	public void mergeOrTypes() throws Exception {
		// Types with explicit names.
		TMField f1 = new TMField("type1");
		TMField f2 = new TMField("type2");
		TMField f3 = new TMField("type3");

		TMField f12 = TMField.merge(f1, f2).withName("f12");
		assertEquals("f12=(type1 | type2)", f12.toString());

		TMField f23 = TMField.merge(f2, f3).withName("f23");
		assertEquals("f23=(type2 | type3)", f23.toString());

		TMField f123 = TMField.merge(f12, f23).withName("f123");
		assertEquals("f123=(type1 | type2 | type3)", f123.toString());

		TMField f123opt = TMField.merge(f12, f23.makeNullable()).withName("f123opt");
		assertEquals("f123opt=(type1 | type2 | type3)?", f123opt.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotMergeEmptyList() throws Exception {
		TMField.merge();
	}
}