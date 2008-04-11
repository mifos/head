/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.framework.util.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Contains a representation of a "chapter number", like "4.1.3", "1.2.10",
 * "1.2.20", etc. The chapter number is a comparable <a
 * href="http://en.wikipedia.org/wiki/Tuple">tuple</a> (or group) of integers.
 * Negative numbers must not be added to this tuple, although no explicit
 * runtime checks exist to ensure this. If {@link #fromString(String)} is used,
 * negative numbers will be filtered out. This is the preferred method for
 * instantiating new objects of this class.
 * 
 * <p>
 * We don't need to override {@link ArrayList#equals(Object)}, the parent
 * version works for us too.
 */
public class ChapterNum extends ArrayList<Integer> implements
		Comparable<List<Integer>> {
	private static final long serialVersionUID = 1L;

	public int compareTo(List<Integer> o) {
		int i = 0, z = 1;

		// lists are equal length, and all elements are equal.
		if (this.equals(o))
			return 0;

		// must compare every element one by one
		while (true) {
			// the first number we encounter that is unequal immediately
			// indicates sort order
			if (this.get(i) < o.get(i))
				return -1;
			if (this.get(i) > o.get(i))
				return 1;

			// at end of both arrays and we're equal. Both must be equal.
			// This case is covered by the above call to the parent class
			// .equals() method
			// if (this.size() == atElemNum && this.size() == o.size())
			// return 0;

			// we're at the end of this array and we're equal so far. Other
			// must have more digits, hence, must be greater.
			if (this.size() == z)
				return -1;

			// we're at the end of the other array and we're equal so far.
			// Other must have less digits, hence, must be less.
			if (o.size() == z)
				return 1;

			// we're equal so far, and both lists have more numbers to
			// compare. increment and continue.
			i++;
			z++;
		}
	}

	/**
	 * Returns a dotted numeric "chapter number", like "1.12.7".
	 */
	@Override
	public String toString() {
		String s = "";
		Iterator<Integer> numiter = this.iterator();
		while (numiter.hasNext()) {
			s += numiter.next();
			if (numiter.hasNext())
				s += '.';
		}
		return s;
	}

	/**
	 * Instantiates a new <code>ChapterNum</code> object based on information
	 * found in the given string.
	 * 
	 * <p>
	 * Dots are discarded and numbers are then parsed as <code>int</code>s.
	 * 
	 * <p>
	 * Strings that don't exactly match the form <code>^\d+(\.\d+)*$</code>
	 * will result in a <code>null</code> return value.
	 * 
	 * @param s
	 *            {@link String} to transform into a <code>ChapterNum</code>.
	 * @return May be null if input does not match expected pattern of a chapter
	 *         number.
	 */
	public static ChapterNum fromString(String s) {
		if (null == s || s.length() < 1 || !s.matches("^\\d+(\\.\\d+)*$"))
			return null;

		// extract digits
		String[] ints = s.split("\\.");
		ChapterNum c = new ChapterNum();
		for (String intStr : ints) {
			c.add(Integer.parseInt(intStr));
		}

		return c;
	}

	/**
	 * Exactly follows specification of
	 * {@link java.util.Comparator#compare(Object, Object)}.
	 */
	public static int compare(ChapterNum c1, ChapterNum c2) {
		return c1.compareTo(c2);
	}

	/**
	 * Exactly follows specification of
	 * {@link java.util.Comparator#compare(Object, Object)}.
	 */
	public static int compare(String s1, String s2) {
		return fromString(s1).compareTo(fromString(s2));
	}
}
